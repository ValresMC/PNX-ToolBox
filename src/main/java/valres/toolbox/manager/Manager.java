package valres.toolbox.manager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.utils.Logger;
import valres.toolbox.manager.annotation.ManagerDependencies;
import valres.toolbox.manager.annotation.ManagerInfo;
import valres.toolbox.manager.annotation.ManagerPriorityInfo;
import valres.toolbox.manager.enums.ManagerPriority;
import valres.toolbox.manager.enums.ManagerState;

public abstract class Manager {
	protected final PluginBase plugin;
	protected final Logger logger;

	private ManagerState state = ManagerState.UNLOADED;

	protected Manager(@NonNull PluginBase plugin) {
		this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
		this.logger = plugin.getLogger();
	}

	public @NonNull PluginBase getPlugin() {
		return this.plugin;
	}

	public @NonNull Logger getLogger() {
		return this.logger;
	}

	public String getName() {
		ManagerInfo info = getClass().getAnnotation(ManagerInfo.class);

		return info != null ? info.name() : getClass().getSimpleName();
	}

	public String getVersion() {
		ManagerInfo info = getClass().getAnnotation(ManagerInfo.class);

		return info != null ? info.version() : "BETA";
	}

	public ManagerPriority getPriority() {
		ManagerPriorityInfo info = getClass().getAnnotation(ManagerPriorityInfo.class);

		return info != null ? info.value() : ManagerPriority.MEDIUM;
	}

	public List<String> getDependencies() {
		ManagerDependencies dependencies = getClass().getAnnotation(ManagerDependencies.class);

		return dependencies == null ? List.of() : Arrays.asList(dependencies.value());
	}

	public ManagerState getState() {
		return this.state;
	}

	final void setState(@NonNull ManagerState state) {
		this.state = Objects.requireNonNull(state, "State cannot be null");
	}

	public boolean isReady() {
		return this.state.isReady();
	}

	protected abstract void onLoad();

	protected abstract void onEnable();

	protected abstract void beforeStop();

	protected abstract void onDisable();
}
