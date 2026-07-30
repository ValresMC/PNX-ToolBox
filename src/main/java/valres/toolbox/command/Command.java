package valres.toolbox.command;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.command.CommandMap;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginIdentifiableCommand;
import org.powernukkitx.command.data.CommandDataVersions;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.permission.Permission;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.plugin.PluginManager;
import valres.toolbox.command.argument.Argument;
import valres.toolbox.command.exception.CommandConfigurationException;
import valres.toolbox.command.exception.CommandInputException;
import valres.toolbox.command.result.CommandFailure;
import valres.toolbox.command.result.CommandFailureReason;
import valres.toolbox.command.result.CommandResult;
import valres.toolbox.command.result.CommandSuccess;
import valres.toolbox.command.rules.PermissionRule;
import valres.toolbox.command.rules.Rule;
import valres.toolbox.command.rules.RuleResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

abstract public class Command extends org.powernukkitx.command.Command implements PluginIdentifiableCommand {
    private Plugin plugin;
    final private CommandNodeData data;

    private String commandPermission;
    private boolean initializing;
    private boolean initialized;

    protected Command() {
        super();

        this.data = new CommandNodeData("pending", "", new String[0]);
    }

    protected Command(Plugin plugin, String name) {
        this(plugin, name, "");
    }

    protected Command(Plugin plugin, String name, String description, String... aliases) {
        super(name, description, null, aliases);

        this.plugin = Objects.requireNonNull(plugin, "Owning plugin cannot be null");
        this.data = new CommandNodeData(name, description, aliases);
    }

    @Override final public synchronized Plugin getPlugin() {
        if (this.plugin == null) {
            this.plugin = resolveOwningPlugin(this.getClass());
        }
        return this.plugin;
    }

    @Override public boolean register(CommandMap commandMap) {
        this.initialize();
        return super.register(commandMap);
    }

    @Override final public void enableCommandTree() {
    }

    @Override final public void enableParamTree() {
    }

    final public synchronized void initialize() {
        if (this.initialized) {
            return;
        }
        if (this.initializing) {
            throw new CommandConfigurationException(
                "Circular initialization detected for command '" + this.getName() + "'"
            );
        }

        this.initializing = true;
        try {
            this.applyNativeMetadata();
            this.data.loadAnnotatedArguments(this.getClass());
            this.resolvePermission();
            this.configure();
            this.getSubCommands().forEach(SubCommand::initialize);
            this.addPermissionRule();

            for (SubCommand subCommand : this.getSubCommands()) {
                subCommand.bindTo(this, List.of(subCommand.getName()));
            }

            this.initialized = true;
            this.rebuildDefinition();
        } finally {
            this.initializing = false;
        }
    }

    protected void configure() {
    }

    abstract protected Object onRun(CommandContext context);

    final public Command addArgument(Argument<?> argument) {
        this.data.addArgument(argument);
        this.refreshDefinition();

        return this;
    }

    final public Argument<?> getArgument(String name) {
        return this.data.getArgument(name);
    }

    final public List<Argument<?>> getArguments() {
        return this.data.getArguments();
    }

    final public Command addRule(Rule rule) {
        this.data.addRule(rule);

        return this;
    }

    final public List<Rule> getRules() {
        return this.data.getRules();
    }

    final public void addSubCommand(SubCommand subCommand) {
        this.data.addSubCommand(subCommand);
        if (this.initialized) {
            subCommand.bindTo(this, List.of(subCommand.getName()));
        }
        this.refreshDefinition();

    }

    final public SubCommand getSubCommand(String label) {
        return this.data.findSubCommand(label);
    }

    final public List<SubCommand> getSubCommands() {
        return this.data.getSubCommands();
    }

    final public String getCommandPermission() {
        if (this.commandPermission == null && !this.initializing) {
            this.initialize();
        }

        return this.commandPermission;
    }

    final public List<String> getUsageLines() {
        this.initialize();

        List<String> lines = new ArrayList<>();
        lines.add(this.data.getUsage(this.getName()));
        for (SubCommand subCommand : this.getSubCommands()) {
            lines.addAll(subCommand.getUsageLines(this.getName()));
        }

        return List.copyOf(lines);
    }

    final public CommandResult dispatch(CommandSender sender, String commandLabel, String[] arguments) {
        Objects.requireNonNull(sender, "Command sender cannot be null");
        Objects.requireNonNull(commandLabel, "Command label cannot be null");
        Objects.requireNonNull(arguments, "Raw command arguments cannot be null");
        this.initialize();

        List<String> rawArguments = List.copyOf(Arrays.asList(arguments));
        List<CommandNodeData> path = new ArrayList<>();
        path.add(this.data);

        CommandNodeData current = this.data;
        SubCommand target = null;
        int cursor = 0;
        String label = commandLabel;
        while (cursor < rawArguments.size()) {
            SubCommand child = current.findSubCommand(rawArguments.get(cursor));
            if (child == null) {
                break;
            }

            target = child;
            current = child.getData();
            path.add(current);
            label += " " + rawArguments.get(cursor);
            cursor++;
        }

        String usage = current.getUsage(label);
        try {
            for (CommandNodeData node : path) {
                RuleResult ruleResult = node.testRules(sender);
                if (!ruleResult.isSuccess()) {
                    return new CommandFailure(
                        sender,
                        CommandFailureReason.RULE_FAILED,
                        CommandMessages.get(CommandMessages.COMMAND_RULE_FAILED),
                        usage,
                        Map.of(),
                        ruleResult.failed(),
                        null
                    );
                }
            }

            List<String> nodeArguments = rawArguments.subList(cursor, rawArguments.size());
            ArgumentsList parsedArguments = current.parse(sender, nodeArguments);
            CommandContext context = new CommandContext(
                sender,
                parsedArguments,
                label,
                nodeArguments,
                this,
                target
            );
            Object returnValue = target == null
                ? this.onRun(context)
                : target.run(context);

            path.forEach(node -> node.notifyExecuted(sender));
            return new CommandSuccess(context, returnValue);
        } catch (CommandInputException exception) {
            return new CommandFailure(
                sender,
                exception.getReason(),
                exception.getMessage(),
                usage,
                exception.getDetails(),
                List.of(),
                exception
            );
        } catch (Throwable throwable) {
            return new CommandFailure(
                sender,
                CommandFailureReason.EXECUTION_ERROR,
                CommandMessages.get(CommandMessages.COMMAND_EXECUTION_ERROR),
                usage,
                Map.of(),
                List.of(),
                throwable
            );
        }
    }

    @Override final public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        CommandResult result = this.dispatch(sender, commandLabel, args);
        if (result instanceof CommandSuccess success) {
            this.success(success);
        } else if (result instanceof CommandFailure failure) {
            this.fail(failure);
        }

        return result.isSuccess();
    }

    @Override public CommandDataVersions generateCustomCommandData(Player player) {
        this.initialize();

        return super.generateCustomCommandData(player);
    }

    protected void success(CommandSuccess result) {
    }

    protected void fail(CommandFailure result) {
        if (result.reason() == CommandFailureReason.RULE_FAILED) {
            return;
        }

        if (!result.message().isEmpty()) {
            result.sender().sendMessage(result.message());
        }
        if (result.usage() != null && result.reason() != CommandFailureReason.EXECUTION_ERROR) {
            CommandMessages.send(
                result.sender(),
                CommandMessages.COMMAND_USAGE,
                Map.of("usage", result.usage())
            );
        }
        if (result.cause() != null && result.reason() == CommandFailureReason.EXECUTION_ERROR) {
            this.getPlugin().getLogger().error(
                "Command /" + this.getName() + " failed", result.cause()
            );
        }
    }

    final void registerPermission(String permission, String description, String defaultValue) {
        PluginManager pluginManager = this.getPlugin().getServer().getPluginManager();
        if (pluginManager.getPermission(permission) != null) {
            return;
        }

        pluginManager.addPermission(new Permission(permission, description, defaultValue));
    }

    final public synchronized void refreshDefinition() {
        if (!this.initialized) {
            return;
        }

        this.rebuildDefinition();
        for (Player player : this.getPlugin().getServer().getOnlinePlayers().values()) {
            player.syncAvailableCommands();
        }
    }

    final List<String> findPath(SubCommand expected) {
        return this.findPath(this.getSubCommands(), expected, new ArrayList<>());
    }

    private List<String> findPath(List<SubCommand> children, SubCommand expected, List<String> parentPath) {
        for (SubCommand child : children) {
            List<String> path = new ArrayList<>(parentPath);
            path.add(child.getName());
            if (child == expected) {
                return List.copyOf(path);
            }

            List<String> nested = this.findPath(child.getSubCommands(), expected, path);
            if (nested != null) {
                return nested;
            }
        }

        return null;
    }

    private void resolvePermission() {
        String nativePermission = this.getPermission();
        this.commandPermission = nativePermission == null || nativePermission.isBlank()
            ? this.getName().toLowerCase(Locale.ROOT) + ".command"
            : nativePermission;

        this.registerPermission(
            this.commandPermission,
            this.getDescription(),
            Permission.DEFAULT_OP
        );
        this.setPermission(this.commandPermission);
    }

    private void applyNativeMetadata() {
        String name = this.getName();
        if (name == null || name.isBlank()) {
            throw new CommandConfigurationException(
                "Command " + this.getClass().getName() + " needs PNX @CommandDefinition or an explicit name constructor"
            );
        }

        this.data.updateMetadata(name, this.getDescription(), this.getAliases());
    }

    private void addPermissionRule() {
        boolean hasRule = this.getRules().stream()
            .filter(PermissionRule.class::isInstance)
            .map(PermissionRule.class::cast)
            .anyMatch(rule -> rule.getPermission().equals(this.commandPermission));
        if (!hasRule) {
            this.data.addRule(new PermissionRule(this.commandPermission));
        }
    }

    private void rebuildDefinition() {
        LinkedHashMap<String, CommandParameter[]> overloads = new LinkedHashMap<>();
        this.addOverloads(overloads, "root", List.of(), this.data);
        this.setCommandParameters(overloads);
        this.setUsage(String.join("\n", this.getUsageLinesWithoutInitialization()));
    }

    private void addOverloads(
        Map<String, CommandParameter[]> overloads,
        String key,
        List<CommandParameter> prefix,
        CommandNodeData node
    ) {
        List<CommandParameter> parameters = new ArrayList<>(prefix);
        node.getArguments().stream()
            .map(Argument::toCommandParameter)
            .forEach(parameters::add);
        overloads.put(key, parameters.toArray(CommandParameter[]::new));

        int depth = prefix.size() + 1;
        for (SubCommand child : node.getSubCommands()) {
            List<String> labels = new ArrayList<>();
            labels.add(child.getName());
            labels.addAll(child.getAliases());

            List<CommandParameter> childPrefix = new ArrayList<>(prefix);
            childPrefix.add(CommandParameter.newEnum(
                "subcommand_" + depth,
                false,
                labels.toArray(String[]::new)
            ));
            this.addOverloads(
                overloads,
                key + "_" + child.getName(),
                childPrefix,
                child.getData()
            );
        }
    }

    private List<String> getUsageLinesWithoutInitialization() {
        List<String> lines = new ArrayList<>();
        lines.add(this.data.getUsage(this.getName()));
        for (SubCommand subCommand : this.getSubCommands()) {
            lines.addAll(subCommand.getUsageLines(this.getName()));
        }

        return lines;
    }

    private static Plugin resolveOwningPlugin(Class<?> commandType) {
        Server server = Server.getInstance();
        if (server == null) {
            throw new CommandConfigurationException(
                "Cannot resolve the owning plugin for command " + commandType.getName() + " before the server is initialized"
            );
        }

        ClassLoader commandLoader = commandType.getClassLoader();
        List<Plugin> matches = server.getPluginManager().getPlugins().values().stream()
            .filter(plugin -> plugin.getClass().getClassLoader() == commandLoader)
            .toList();

        if (matches.size() != 1) {
            throw new CommandConfigurationException(
                "Unable to resolve exactly one owning plugin for command " + commandType.getName() + "; found " + matches.size()
            );
        }

        return matches.getFirst();
    }
}
