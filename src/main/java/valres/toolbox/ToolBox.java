package valres.toolbox;

import org.powernukkitx.plugin.PluginBase;
import valres.toolbox.manager.ManagersHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

final public class ToolBox extends PluginBase {
    private static ToolBox INSTANCE;

    final private Map<PluginBase, ManagersHandler> handlers = new ConcurrentHashMap<>();

    public static ToolBox getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "PNX-ToolBox is not enabled."
            );
        }

        return INSTANCE;
    }

    @Override public void onLoad() {
        INSTANCE = this;
    }

    @Override public void onDisable() {
        this.handlers.clear();
        INSTANCE = null;
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
