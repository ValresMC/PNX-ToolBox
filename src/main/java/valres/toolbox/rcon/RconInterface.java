package valres.toolbox.rcon;

import org.powernukkitx.utils.Logger;
import valres.toolbox.rcon.exception.RconProtocolException;
import valres.toolbox.rcon.exception.RconSocketException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RconInterface implements AutoCloseable {
    final private static int SERVERDATA_RESPONSE_VALUE = 0;
    final private static int SERVERDATA_EXECCOMMAND = 2;
    final private static int SERVERDATA_AUTH_RESPONSE = 2;
    final private static int SERVERDATA_AUTH = 3;
    final private static int MAX_RESPONSE_PAYLOAD_BYTES = 4_096;

    final private RconSettings settings;
    final private Logger logger;
    final private RconCommandExecutor commandExecutor;
    final private byte[] expectedPassword;
    final private Semaphore clientSlots;
    final private Set<Socket> clients = ConcurrentHashMap.newKeySet();
    final private AtomicBoolean running = new AtomicBoolean();

    private volatile ServerSocket serverSocket;
    private volatile Thread acceptThread;
    private volatile ExecutorService connectionExecutor;
    private volatile ExecutorService executionExecutor;
    private boolean started;

    public RconInterface(RconSettings settings, Logger logger, RconCommandExecutor commandExecutor) {
        this.settings = Objects.requireNonNull(settings, "RCON settings cannot be null");
        this.logger = Objects.requireNonNull(logger, "RCON logger cannot be null");
        this.commandExecutor = Objects.requireNonNull(commandExecutor, "RCON command executor cannot be null");
        this.expectedPassword = settings.getPassword().getBytes(StandardCharsets.UTF_8);
        this.clientSlots = new Semaphore(settings.getMaxClients());
    }

    public synchronized void start() {
        if (this.started) {
            throw new RconSocketException(
                "RCON interface can only be started once", null
            );
        }

        ServerSocket listener = null;
        try {
            listener = new ServerSocket();
            listener.setReuseAddress(true);
            listener.bind(new InetSocketAddress(this.settings.getAddress(), this.settings.getPort()), this.settings.getListenBacklog());
        } catch (IOException | RuntimeException exception) {
            closeQuietly(listener);
            throw new RconSocketException(
                "Unable to bind RCON to " + this.settings.getAddress() + ":" + this.settings.getPort(), exception
            );
        }

        this.serverSocket = listener;
        this.connectionExecutor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("RCON-client-", 0).factory()
        );
        this.executionExecutor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("RCON-command-", 0).factory()
        );
        this.started = true;
        this.running.set(true);
        this.acceptThread = Thread.ofPlatform().name("RCON-listener").daemon(true).start(this::acceptConnections);
    }

    private void acceptConnections() {
        try {
            while (this.running.get()) {
                Socket client = this.serverSocket.accept();
                if (!this.clientSlots.tryAcquire()) {
                    closeQuietly(client);
                    continue;
                }

                this.clients.add(client);
                try {
                    this.connectionExecutor.submit(() -> this.handleClient(client));
                } catch (RejectedExecutionException exception) {
                    this.clients.remove(client);
                    this.clientSlots.release();
                    closeQuietly(client);
                }
            }
        } catch (SocketException exception) {
            if (this.running.get()) {
                this.logger.error("RCON listener stopped because of a socket error", exception);
            }
        } catch (IOException exception) {
            if (this.running.get()) {
                this.logger.error("RCON listener stopped because of an I/O error", exception);
            }
        } finally {
            if (this.running.compareAndSet(true, false)) {
                this.closeNetworkResources(false);
            }
        }
    }

    private void handleClient(Socket client) {
        try (client) {
            client.setTcpNoDelay(true);
            long authDeadline = System.nanoTime() + this.settings.getAuthenticationTimeout().toNanos();
            RconPacket authentication = this.readPacket(client, authDeadline);

            if (authentication.getType() != SERVERDATA_AUTH || !MessageDigest.isEqual(authentication.getPayload().getBytes(StandardCharsets.UTF_8), this.expectedPassword)) {
                this.writePacket(client, new RconPacket(-1, SERVERDATA_AUTH_RESPONSE, ""));
                return;
            }

            this.writePacket(client, new RconPacket(authentication.getRequestId(), SERVERDATA_AUTH_RESPONSE, ""));
            client.setSoTimeout(0);

            while (this.running.get() && !client.isClosed()) {
                RconPacket packet = this.readPacket(client, Long.MAX_VALUE);
                if (packet.getType() == SERVERDATA_EXECCOMMAND) {
                    String command = packet.getPayload().trim();
                    String response = this.executeCommand(command);
                    if (response == null) {
                        return;
                    }
                    this.writeResponse(client, packet.getRequestId(), normalizeLineEndings(response));
                } else if (packet.getType() == SERVERDATA_AUTH) {
                    this.writePacket(client, new RconPacket(-1, SERVERDATA_AUTH_RESPONSE, ""));
                    return;
                }
            }
        } catch (EOFException | SocketException ignored) {
        } catch (SocketTimeoutException exception) {
            this.logger.debug("RCON client did not authenticate before the timeout");
        } catch (RconProtocolException exception) {
            this.logger.debug("RCON client sent an invalid packet: " + exception.getMessage());
        } catch (IOException exception) {
            if (this.running.get()) {
                this.logger.debug("RCON client connection failed", exception);
            }
        } finally {
            this.clients.remove(client);
            this.clientSlots.release();
        }
    }

    private String executeCommand(String command) {
        Future<String> execution;
        try {
            execution = this.executionExecutor.submit(() -> this.commandExecutor.execute(command));
        } catch (RejectedExecutionException exception) {
            return null;
        }

        try {
            String response = execution.get(this.settings.getCommandTimeout().toMillis(), TimeUnit.MILLISECONDS);
            return response == null ? "" : response;
        } catch (TimeoutException exception) {
            execution.cancel(true);
            this.logger.warning("RCON command timed out: " + command);
            return "Command timed out.";
        } catch (InterruptedException exception) {
            execution.cancel(true);
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            this.logger.error("RCON command failed: " + command, cause);
            return "Command failed. See the server log for details.";
        }
    }

    private RconPacket readPacket(Socket socket, long deadlineNanos) throws IOException {
        InputStream input = socket.getInputStream();
        byte[] sizeBytes = new byte[Integer.BYTES];
        readFully(socket, input, sizeBytes, deadlineNanos);

        int bodySize = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (bodySize < RconPacket.MINIMUM_BODY_SIZE || bodySize > this.settings.getMaxPacketSize()) {
            throw new RconProtocolException(
                "RCON packet length " + bodySize + " is outside the configured limits"
            );
        }

        byte[] body = new byte[bodySize];
        readFully(socket, input, body, deadlineNanos);
        return RconPacket.decode(body);
    }

    private static void readFully(Socket socket, InputStream input, byte[] destination, long deadlineNanos)
            throws IOException {
        int offset = 0;
        while (offset < destination.length) {
            if (deadlineNanos != Long.MAX_VALUE) {
                long remainingNanos = deadlineNanos - System.nanoTime();
                if (remainingNanos <= 0) {
                    throw new SocketTimeoutException(
                        "RCON authentication timed out"
                    );
                }
                long remainingMillis = TimeUnit.NANOSECONDS.toMillis(remainingNanos);
                socket.setSoTimeout((int) Math.min(Integer.MAX_VALUE, Math.max(1, remainingMillis)));
            }

            int read = input.read(destination, offset, destination.length - offset);
            if (read < 0) {
                throw new EOFException(
                    "RCON connection closed in the middle of a packet"
                );
            }
            offset += read;
        }
    }

    private void writeResponse(Socket socket, int requestId, String response) throws IOException {
        if (response.isEmpty()) {
            this.writePacket(socket, new RconPacket(requestId, SERVERDATA_RESPONSE_VALUE, ""));
            return;
        }

        StringBuilder chunk = new StringBuilder();
        int chunkBytes = 0;
        for (int offset = 0; offset < response.length();) {
            int codePoint = response.codePointAt(offset);
            String character = new String(Character.toChars(codePoint));
            int characterBytes = character.getBytes(StandardCharsets.UTF_8).length;

            if (chunkBytes + characterBytes > MAX_RESPONSE_PAYLOAD_BYTES && !chunk.isEmpty()) {
                this.writePacket(socket, new RconPacket(requestId, SERVERDATA_RESPONSE_VALUE, chunk.toString()));
                chunk.setLength(0);
                chunkBytes = 0;
            }

            chunk.append(character);
            chunkBytes += characterBytes;
            offset += Character.charCount(codePoint);
        }

        if (!chunk.isEmpty()) {
            this.writePacket(socket, new RconPacket(requestId, SERVERDATA_RESPONSE_VALUE, chunk.toString()));
        }
    }

    private void writePacket(Socket socket, RconPacket packet) throws IOException {
        OutputStream output = socket.getOutputStream();
        output.write(packet.encode());
        output.flush();
    }

    private static String normalizeLineEndings(String response) {
        return response.replace("\r\n", "\n").replace('\r', '\n').replace("\n", "\r\n");
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public void shutdown() {
        if (!this.running.compareAndSet(true, false)) {
            return;
        }
        this.closeNetworkResources(true);
    }

    private void closeNetworkResources(boolean waitForListener) {
        closeQuietly(this.serverSocket);
        for (Socket client : this.clients) {
            closeQuietly(client);
        }

        shutdownExecutor(this.connectionExecutor);
        shutdownExecutor(this.executionExecutor);

        Thread listener = this.acceptThread;
        if (waitForListener && listener != null && listener != Thread.currentThread()) {
            try {
                listener.join(5_000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    @Override public void close() {
        this.shutdown();
    }
}
