package valres.toolbox.manager;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.utils.Logger;
import valres.toolbox.manager.enums.ManagerPriority;
import valres.toolbox.manager.enums.ManagerState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

final public class ManagersHandler {
    final private static Comparator<Manager> MANAGER_ORDER = ManagerPriority.comparator();

    final private PluginBase plugin;
    final private Logger logger;

    final private Map<String, Manager> managers = new LinkedHashMap<>();
    final private List<Manager> orderedManagers = new ArrayList<>();

    public ManagersHandler(@NonNull PluginBase plugin) {
        this.plugin = Objects.requireNonNull(
            plugin,
            "Plugin cannot be null"
        );
        this.logger = plugin.getLogger();
    }

    public @NonNull PluginBase getPlugin() {
        return this.plugin;
    }

    public synchronized void registerManager(@NonNull Manager... managers) {
        Objects.requireNonNull(managers, "Managers cannot be null");

        for (Manager manager : managers) {
            Objects.requireNonNull(manager, "Manager cannot be null");

            String name = manager.getName();

            if (this.managers.containsKey(name)) {
                throw new IllegalStateException(
                    "Manager '" + name + "' is already registered"
                );
            }

            this.managers.put(name, manager);
        }
    }

    public synchronized void onLoad() {
        int length = this.sortManagers();
        long startTime = System.nanoTime();

        this.logger.info(length + " managers found");

        for (Manager manager : this.orderedManagers) {
            manager.onLoad();
            manager.setState(ManagerState.LOADED);
        }

        this.logger.info(length + " managers loaded in " + this.formatElapsedTime(startTime));
    }

    public synchronized void onEnable() {
        for (Manager manager : this.orderedManagers) {
            long startTime = System.nanoTime();

            manager.onEnable();
            manager.setState(ManagerState.ENABLED);

            this.logger.info(manager.getName() + " (" + manager.getVersion() + ") enabled in " + this.formatElapsedTime(startTime));
        }
    }

    public synchronized void beforeStop() {
        for (int i = this.orderedManagers.size() - 1; i >= 0; i--) {
            this.orderedManagers.get(i).beforeStop();
        }
    }

    public synchronized void onDisable() {
        int length = this.orderedManagers.size();
        long startTime = System.nanoTime();

        for (int i = this.orderedManagers.size() - 1; i >= 0; i--) {
            Manager manager = this.orderedManagers.get(i);

            manager.onDisable();
            manager.setState(ManagerState.DISABLED);
        }

        this.logger.info(length + " managers disabled in " + this.formatElapsedTime(startTime));
    }

    private int sortManagers() {
        this.orderedManagers.clear();
        this.orderedManagers.addAll(this.managers.values());
        this.orderedManagers.sort(MANAGER_ORDER);

        return this.orderedManagers.size();
    }

    private String formatElapsedTime(long startTime) {
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;

        return String.format(Locale.US, "%.3f sec.", elapsedSeconds);
    }
}
