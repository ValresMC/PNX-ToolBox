package valres.toolbox.rcon;

import org.junit.jupiter.api.Test;
import org.powernukkitx.utils.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class RconInterfaceTest {
    @Test
    void authenticatesAndExecutesCommand() throws IOException {
        int port = findAvailablePort();
        RconSettings settings = RconSettings.defaults("secret", port, "127.0.0.1");
        RconInterface rcon = new RconInterface(
                settings,
                noOpLogger(),
                command -> "ran " + command + "\nsecond line"
        );

        rcon.start();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            socket.setSoTimeout(2_000);
            writePacket(socket, new RconPacket(10, 3, "secret"));

            RconPacket authentication = readPacket(socket);
            assertEquals(10, authentication.getRequestId());
            assertEquals(2, authentication.getType());

            writePacket(socket, new RconPacket(11, 2, "  list  "));
            RconPacket response = readPacket(socket);
            assertEquals(11, response.getRequestId());
            assertEquals(0, response.getType());
            assertEquals("ran list\r\nsecond line", response.getPayload());
        } finally {
            rcon.shutdown();
        }

        assertFalse(rcon.isRunning());
    }

    @Test
    void acceptsAFragmentedAuthenticationPacket() throws IOException {
        int port = findAvailablePort();
        RconInterface rcon = new RconInterface(
                RconSettings.defaults("secret", port, "127.0.0.1"),
                noOpLogger(),
                command -> command
        );

        rcon.start();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            socket.setSoTimeout(2_000);
            for (byte value : new RconPacket(7, 3, "secret").encode()) {
                socket.getOutputStream().write(value);
                socket.getOutputStream().flush();
            }
            assertEquals(7, readPacket(socket).getRequestId());
        } finally {
            rcon.shutdown();
        }
    }

    @Test
    void rejectsInvalidPassword() throws IOException {
        int port = findAvailablePort();
        RconInterface rcon = new RconInterface(
                RconSettings.defaults("secret", port, "127.0.0.1"),
                noOpLogger(),
                command -> "unexpected"
        );

        rcon.start();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            socket.setSoTimeout(2_000);
            writePacket(socket, new RconPacket(12, 3, "wrong"));
            RconPacket response = readPacket(socket);
            assertEquals(-1, response.getRequestId());
            assertEquals(2, response.getType());
        } finally {
            rcon.shutdown();
        }
    }

    @Test
    void timesOutSlowCommands() throws IOException {
        int port = findAvailablePort();
        RconSettings settings = new RconSettings(
                "127.0.0.1", port, "secret", 1, Duration.ofSeconds(1), 1, 1024, Duration.ofMillis(50)
        );
        RconInterface rcon = new RconInterface(settings, noOpLogger(), command -> {
            Thread.sleep(5_000);
            return "late";
        });

        rcon.start();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            socket.setSoTimeout(2_000);
            writePacket(socket, new RconPacket(1, 3, "secret"));
            readPacket(socket);
            writePacket(socket, new RconPacket(2, 2, "slow"));
            assertEquals("Command timed out.", readPacket(socket).getPayload());
        } finally {
            rcon.shutdown();
        }
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void writePacket(Socket socket, RconPacket packet) throws IOException {
        socket.getOutputStream().write(packet.encode());
        socket.getOutputStream().flush();
    }

    private static RconPacket readPacket(Socket socket) throws IOException {
        DataInputStream input = new DataInputStream(socket.getInputStream());
        int bodySize = Integer.reverseBytes(input.readInt());
        byte[] body = new byte[bodySize];
        input.readFully(body);
        return RconPacket.decode(body);
    }

    private static Logger noOpLogger() {
        return (Logger) Proxy.newProxyInstance(
                Logger.class.getClassLoader(),
                new Class<?>[]{Logger.class},
                (proxy, method, arguments) -> null
        );
    }
}
