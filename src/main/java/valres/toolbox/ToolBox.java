package valres.toolbox;

import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.scheduler.Task;
import org.powernukkitx.scheduler.TaskHandler;
import org.powernukkitx.utils.Config;
import valres.toolbox.behavior.item.CustomItemRegistry;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.listener.ItemRegistryPacketListener;
import valres.toolbox.manager.ManagersHandler;
import valres.toolbox.rcon.Rcon;
import valres.toolbox.rcon.RconCommandExecutor;
import valres.toolbox.rcon.RconSettings;
import valres.toolbox.rcon.exception.RconException;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

final public class ToolBox extends PluginBase {
    private static ToolBox INSTANCE;

    final private Map<PluginBase, ManagersHandler> handlers = new ConcurrentHashMap<>();

    private Rcon rcon;
    private TaskHandler rconTask;

    public static ToolBox getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                "PNX-ToolBox is not enabled."
            );
        }

        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;

        this.saveResource("rcon-config.yml");
        CommandMessages.load(this);
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ItemRegistryPacketListener(CustomItemRegistry.getInstance()), this);
        
        Config config = new Config(this.getDataFolder() + "/rcon-config.yml");
        if (!config.getBoolean("enabled", false)) {
            this.getLogger().info("RCON is disabled");
            return;
        }

        try {
            this.startRcon(new RconSettings(
                config.getString("address", "127.0.0.1"),
                config.getInt("port", 30099),
                config.getString("password", ""),
                config.getInt("max-clients", RconSettings.DEFAULT_MAX_CLIENTS),
                Duration.ofMillis(config.getInt("authentication-timeout-ms", 5_000)),
                config.getInt("listen-backlog", RconSettings.DEFAULT_LISTEN_BACKLOG),
                config.getInt("max-packet-size", RconSettings.DEFAULT_MAX_PACKET_SIZE),
                Duration.ofMillis(config.getInt("command-timeout-ms", 10_000))
            ));
        } catch (RconException | ArithmeticException exception) {
            this.getLogger().error("Unable to start RCON; check rcon-config.yml", exception);
        }
    }

    @Override
    public void onDisable() {
        this.stopRcon();

        this.handlers.clear();
        CommandMessages.reset();
        INSTANCE = null;
    }

    public synchronized void startRcon(RconSettings settings) {
        this.startRcon(settings, null);
    }

    public synchronized void startRcon(RconSettings settings, RconCommandExecutor commandExecutor) {
        Objects.requireNonNull(settings, "RCON settings cannot be null");
        if (this.rcon != null) {
            throw new RconException(
                "An RCON interface is already running"
            );
        }

        Rcon startedRcon = new Rcon(this.getServer(), this.getLogger(), settings, commandExecutor);
        try {
            TaskHandler startedTask = this.getServer().getScheduler().scheduleRepeatingTask(new Task() {
                @Override
                public void onRun(int currentTick) {
                    Rcon activeRcon = rcon;
                    if (activeRcon != null) {
                        activeRcon.check();
                    }
                }
            }, 1);

            this.rcon = startedRcon;
            this.rconTask = startedTask;
            this.getLogger().info("RCON is listening on " + settings.getAddress() + ":" + settings.getPort());
        } catch (RuntimeException exception) {
            startedRcon.close();
            throw exception;
        }
    }

    public synchronized void stopRcon() {
        TaskHandler task = this.rconTask;
        this.rconTask = null;
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        Rcon activeRcon = this.rcon;
        this.rcon = null;
        if (activeRcon != null) {
            activeRcon.close();
        }
    }

    public synchronized boolean isRconRunning() {
        return this.rcon != null && this.rcon.isRunning();
    }

    public void reloadCommandMessages() {
        CommandMessages.reload();
    }

    public ManagersHandler createManagerHandler(PluginBase plugin) {
        Objects.requireNonNull(plugin, "Plugin cannot be null");

        ManagersHandler handler = new ManagersHandler(plugin);
        ManagersHandler previous = this.handlers.putIfAbsent(plugin, handler);

        if (previous != null) {
            throw new IllegalStateException(
                "A manager handler already exists for " + plugin.getName()
            );
        }

        return handler;
    }

    public ManagersHandler getManagerHandler(PluginBase plugin) {
        ManagersHandler handler = this.handlers.get(plugin);

        if (handler == null) {
            throw new IllegalStateException(
                "No manager handler registered for " + plugin.getName()
            );
        }

        return handler;
    }

    public void removeManagerHandler(PluginBase plugin) {
        this.handlers.remove(plugin);
    }
}
