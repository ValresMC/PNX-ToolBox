package valres.toolbox.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import valres.toolbox.command.annotation.SubCommandDefinition;
import valres.toolbox.command.argument.Argument;
import valres.toolbox.command.exception.CommandConfigurationException;
import valres.toolbox.command.rules.PermissionRule;
import valres.toolbox.command.rules.Rule;

public abstract class SubCommand {
	private final CommandNodeData data;

	private Command command;
	private String permission;
	private boolean initializing;
	private boolean initialized;

	protected SubCommand() {
		SubCommandDefinition info = this.getClass().getAnnotation(SubCommandDefinition.class);
		if (info == null) {
			throw new CommandConfigurationException("Sub-command " + this.getClass().getName() + " needs @SubCommandDefinition or an explicit constructor");
		}

		this.data = new CommandNodeData(info.name(), info.description(), info.aliases());
	}

	protected SubCommand(String name) {
		this(name, "");
	}

	protected SubCommand(String name, String description, String... aliases) {
		this.data = new CommandNodeData(name, description, aliases);
	}

	public final String getName() {
		return this.data.getName();
	}

	public final String getDescription() {
		return this.data.getDescription();
	}

	public final List<String> getAliases() {
		return this.data.getAliases();
	}

	public final boolean matches(String label) {
		return this.getName().equalsIgnoreCase(label) || this.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(label));
	}

	public final void addArgument(Argument<?> argument) {
		this.data.addArgument(argument);
		this.refreshRoot();
	}

	public final Argument<?> getArgument(String name) {
		return this.data.getArgument(name);
	}

	public final List<Argument<?>> getArguments() {
		return this.data.getArguments();
	}

	public final SubCommand addRule(Rule rule) {
		this.data.addRule(rule);

		return this;
	}

	public final List<Rule> getRules() {
		return this.data.getRules();
	}

	public final SubCommand addSubCommand(SubCommand subCommand) {
		this.data.addSubCommand(subCommand);
		if (this.command != null) {
			List<String> path = new ArrayList<>(this.getPath());
			path.add(subCommand.getName());
			subCommand.bindTo(this.command, path);
		}
		this.refreshRoot();

		return this;
	}

	public final SubCommand getSubCommand(String label) {
		return this.data.findSubCommand(label);
	}

	public final List<SubCommand> getSubCommands() {
		return this.data.getSubCommands();
	}

	public final Command getCommand() {
		return this.command;
	}

	public final String getPermission() {
		return this.permission;
	}

	public final String getUsage(String parentLabel) {
		return this.data.getUsage(parentLabel + " " + this.getName());
	}

	public final List<String> getUsageLines(String parentLabel) {
		String label = parentLabel + " " + this.getName();
		List<String> lines = new ArrayList<>();
		lines.add(this.data.getUsage(label));
		for (SubCommand subCommand : this.getSubCommands()) {
			lines.addAll(subCommand.getUsageLines(label));
		}

		return List.copyOf(lines);
	}

	protected void configure() {
	}

	protected abstract Object onRun(CommandContext context);

	final Object run(CommandContext context) {
		return this.onRun(context);
	}

	final CommandNodeData getData() {
		return this.data;
	}

	final void initialize() {
		if (this.initialized) {
			return;
		}
		if (this.initializing) {
			throw new CommandConfigurationException("Circular initialization detected for sub-command '" + this.getName() + "'");
		}

		this.initializing = true;
		try {
			this.data.loadAnnotatedArguments(this.getClass());
			this.configure();
			this.getSubCommands().forEach(SubCommand::initialize);
			this.initialized = true;
		} finally {
			this.initializing = false;
		}
	}

	final void bindTo(@NonNull Command command, List<String> path) {
		this.initialize();

		if (this.command != null && this.command != command) {
			throw new CommandConfigurationException("Sub-command '" + this.getName() + "' is already bound to another root command");
		}
		this.command = command;

		String generatedPermission = command.getCommandPermission() + "." + String.join(".", path);

		if (!Objects.equals(this.permission, generatedPermission)) {
			this.permission = generatedPermission;
			command.registerPermission(generatedPermission, this.getDescription(), org.powernukkitx.permission.Permission.DEFAULT_OP);
			boolean hasRule = this.getRules().stream().filter(PermissionRule.class::isInstance).map(PermissionRule.class::cast).anyMatch(rule -> rule.getPermission().equals(generatedPermission));
			if (!hasRule) {
				this.data.addRule(new PermissionRule(generatedPermission));
			}
		}

		for (SubCommand subCommand : this.getSubCommands()) {
			List<String> childPath = new ArrayList<>(path);
			childPath.add(subCommand.getName());
			subCommand.bindTo(command, childPath);
		}
	}

	private List<String> getPath() {
		if (this.command == null) {
			return List.of(this.getName());
		}

		List<String> path = this.command.findPath(this);
		if (path == null) {
			throw new CommandConfigurationException("Unable to resolve path for sub-command '" + this.getName() + "'");
		}

		return path;
	}

	private void refreshRoot() {
		if (this.command != null) {
			this.command.refreshDefinition();
		}
	}
}
