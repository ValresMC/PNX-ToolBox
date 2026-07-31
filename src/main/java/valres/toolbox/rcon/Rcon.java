package valres.toolbox.rcon;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.Server;
import org.powernukkitx.event.server.RemoteServerCommandEvent;
import org.powernukkitx.utils.Logger;
import org.powernukkitx.utils.TextFormat;
import valres.toolbox.rcon.exception.RconException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Rcon implements AutoCloseable {
    final private static int MAX_COMMANDS_PER_TICK = 64;

    final private Server server;
    final private RconCommandExecutor commandExecutor;
    final private RconInterface rconInterface;
    final private ConcurrentLinkedQueue<PendingCommand> pendingCommands = new ConcurrentLinkedQueue<>();
    final private AtomicBoolean closed = new AtomicBoolean();

    public Rcon(
        @NonNull Server server,
        @NonNull String password,
        @NonNull String address,
        int port
    ) {
        this(server, server.getLogger(), RconSettings.defaults(password, port, address), null);
    }

    public Rcon(
        @NonNull Server server,
        @NonNull Logger logger,
        @NonNull RconSettings settings
    ) {
        this(server, logger, settings, null);
    }

    public Rcon(
        @NonNull Server server,
        @NonNull Logger logger,
        @NonNull RconSettings settings,
        RconCommandExecutor commandExecutor
    ) {
        this.server = Objects.requireNonNull(server, "Server cannot be null");
        Objects.requireNonNull(logger, "Logger cannot be null");
        this.commandExecutor = commandExecutor == null ? this::executePowerNukkitCommand : commandExecutor;
        this.rconInterface = new RconInterface(settings, logger, this::waitForPrimaryThread);
        this.rconInterface.start();
    }

    public void check() {
        if (this.closed.get()) {
            return;
        }

        for (int processed = 0; processed < MAX_COMMANDS_PER_TICK; processed++) {
            PendingCommand pending = this.pendingCommands.poll();
            if (pending == null) {
                return;
            }
            if (pending.result().isDone()) {
                continue;
            }

            try {
                pending.result().complete(this.commandExecutor.execute(pending.command()));
            } catch (Throwable exception) {
                pending.result().completeExceptionally(exception);
            }
        }
    }

    private String waitForPrimaryThread(String command) throws Exception {
        if (this.closed.get()) {
            throw new RconException(
                "RCON is shutting down"
            );
        }

        PendingCommand pending = new PendingCommand(command, new CompletableFuture<>());
        this.pendingCommands.add(pending);

        try {
            return pending.result().get();
        } catch (InterruptedException exception) {
            this.pendingCommands.remove(pending);
            pending.result().cancel(false);
            Thread.currentThread().interrupt();
            throw exception;
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof Exception checked) {
                throw checked;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new RconException(
                "RCON command execution failed", cause
            );
        }
    }

    private String executePowerNukkitCommand(String command) {
        RconCommandSender sender = new RconCommandSender();
        RemoteServerCommandEvent event = new RemoteServerCommandEvent(sender, command);
        this.server.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.server.executeCommand(sender, event.getCommand());
        }

        String output = TextFormat.clean(sender.getMessages());
        return output == null ? "" : output;
    }

    public boolean isRunning() {
        return !this.closed.get() && this.rconInterface.isRunning();
    }

    @Override
    public void close() {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }

        RconException shutdown = new RconException(
            "RCON was stopped before the command could run"
        );
        PendingCommand pending;
        while ((pending = this.pendingCommands.poll()) != null) {
            pending.result().completeExceptionally(shutdown);
        }
        this.rconInterface.shutdown();
    }

    private record PendingCommand(String command, CompletableFuture<String> result) {
    }
}
