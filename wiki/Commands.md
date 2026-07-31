# Commands

The command framework provides nested subcommands, typed arguments, reusable rules, generated permissions, Bedrock overloads and runtime refresh.

Instead of parsing raw strings in a large `execute()` method, each command node describes its own arguments and rules. The framework validates the input before calling your handler.

## Registering commands

Root commands use PNX's native `@CommandDefinition`. Its annotation processor
constructs, configures and registers the command, so no `CommandRegistry` or
manual registration is needed:

```java
@PluginMeta(
    name = "ExamplePlugin",
    version = "1.0.0",
    api = {"3.0.0"},
    depend = {"PNX-ToolBox"}
)
public final class ExamplePlugin extends PluginBase {
    private final CoinsService coins = new CoinsService();

    public CoinsService coins() {
        return this.coins;
    }
}
```

`@PluginMeta` is required for the native PNX processor to generate its plugin
bootstrap. Explicit constructors and `CommandRegistry` remain available only
for commands that must be created dynamically at runtime. The native
`commandMode` can keep its default value: the toolbox disables PNX's route tree
and installs its own typed overloads during registration.

## Root command and subcommand

This example creates `/coins give <player> <amount>`.

### Root command

```java
@CommandDefinition(
    name = "coins",
    description = "Manage player coins",
    aliases = {"money"},
    permission = "example.coins"
)
public final class CoinsCommand extends Command {
    @Override
    protected void configure() {
        ExamplePlugin plugin = (ExamplePlugin) this.getPlugin();
        this.addSubCommand(new GiveCoinsSubCommand(plugin.coins()));
    }

    @Override
    protected Object onRun(CommandContext context) {
        context.reply("Usage: /coins give <player> <amount>");
        return null;
    }
}
```

### `give` subcommand

```java
@SubCommandDefinition(name = "give", description = "Give coins to a player")
@CommandArgument(
    order = 0,
    name = "player",
    type = CommandArgumentType.PLAYER
)
@CommandArgument(
    order = 1,
    name = "amount",
    type = CommandArgumentType.INTEGER
)
public final class GiveCoinsSubCommand extends SubCommand {
    private final CoinsService coins;

    public GiveCoinsSubCommand(CoinsService coins) {
        this.coins = coins;
    }

    @Override
    protected void configure() {
        this.addRule(Rules.cooldown(Duration.ofSeconds(2)));
        this.addRule(Rules.predicate(
            sender -> this.coins.isAvailable(),
            "The coin service is currently unavailable."
        ));
    }

    @Override
    protected Object onRun(CommandContext context) {
        Player player = context.arguments().player("player");
        int amount = context.arguments().integer("amount");

        this.coins.add(player, amount);
        context.reply("Added " + amount + " coins to " + player.getName() + ".");
        return amount;
    }
}
```

Subcommands can contain other subcommands by calling `addSubCommand()` in their `configure()` method.
PNX does not support `@CommandDefinition` on non-command nodes, so toolbox
subcommands use the dedicated `@SubCommandDefinition` annotation.

## Arguments

Arguments can be declared with repeatable `@CommandArgument` annotations or added programmatically in `configure()`.

### Annotation style

```java
@CommandArgument(order = 0, name = "player", type = CommandArgumentType.PLAYER)
@CommandArgument(order = 1, name = "amount", type = CommandArgumentType.INTEGER)
@CommandArgument(
    order = 2,
    name = "silent",
    type = CommandArgumentType.BOOLEAN,
    optional = true,
    defaultValue = "false"
)
```

Supported annotation types are:

- `STRING` and greedy `TEXT`
- `INTEGER` and `FLOAT`
- `BOOLEAN` and `ENUM`
- `PLAYER` and entity `TARGET`
- `VECTOR_3` and `BLOCK_POSITION`
- `LEVEL` and `UUID`

### Builder style

```java
@Override
protected void configure() {
    this.addArgument(Arguments.player("player"));
    this.addArgument(Arguments.integer("amount", 1, 10_000));
    this.addArgument(Arguments.bool("silent", false));
}
```

The `Arguments` factory also provides strings, text, floats, enums, dynamic enums, targets, vectors, block positions, levels and UUIDs.

Read parsed values from `context.arguments()`:

```java
Player player = context.arguments().player("player");
int amount = context.arguments().integer("amount");
boolean silent = context.arguments().bool("silent");

Optional<String> note = context.arguments().optional("note", String.class);
```

Required arguments must come before optional arguments. A greedy `TEXT` argument must be the last argument on its command node.

## Rules

Rules run before the command handler. Rules on the root command and every matched subcommand are evaluated.

```java
@Override
protected void configure() {
    this.addRule(Rules.onlyPlayer());
    this.addRule(Rules.permission("example.coins.use"));
    this.addRule(Rules.cooldown(Duration.ofSeconds(5)));
    this.addRule(Rules.predicate(
        sender -> maintenanceService.commandsAllowed(),
        "Commands are disabled during maintenance."
    ));
}
```

Available rules:

- `Rules.onlyPlayer()`
- `Rules.onlyConsole()`
- `Rules.onlyRcon()`
- `Rules.permission("permission.node")`
- `Rules.cooldown(Duration)`
- `Rules.predicate(Predicate<CommandSender>, failureMessage)`

Set the root permission with `@CommandDefinition(permission = "example.coins")`.
When the field is empty, a root command named `coins` receives `coins.command`.
Subcommand permissions are always derived from the root: `give` receives
`example.coins.give` in this example. All generated permissions default to
operators. Add `Rules.permission(...)` when a node needs an additional check.

## Live refresh

Use a dynamic enum when completion values come from runtime data:

```java
@CommandDefinition(name = "warp", description = "Teleport to a warp")
public final class WarpCommand extends Command {
    private WarpService warps;
    private DynamicEnumArgument warpArgument;

    @Override
    protected void configure() {
        this.warps = ((ExamplePlugin) this.getPlugin()).warps();
        this.warpArgument = Arguments.dynamicEnum("warp", this.warps::names);
        this.addArgument(this.warpArgument);
        this.addRule(Rules.onlyPlayer());
    }

    @Override
    protected Object onRun(CommandContext context) {
        this.warps.teleport(context.getPlayer(), context.arguments().string("warp"));
        return null;
    }

    public void refreshWarps() {
        this.warpArgument.refresh();
    }
}
```

Call `DynamicEnumArgument.refresh()` after changing a soft enum's values. Call `Command.refreshDefinition()` after changing the structure of a command. Adding an argument or subcommand through the API refreshes the root definition automatically.

## Before: native PowerNukkitX

`@CommandDefinition` handles native registration, but a plain PNX command must
still declare overloads, identify subcommands, parse arguments and handle errors
itself:

```java
@CommandDefinition(
    name = "coins",
    description = "Manage player coins",
    permission = "example.coins"
)
public final class CoinsCommand extends org.powernukkitx.command.Command {
    public CoinsCommand() {
        super();

        this.setCommandParameters(Map.of(
            "give",
            new CommandParameter[] {
                CommandParameter.newEnum("give", false, new String[] {"give"}),
                CommandParameter.newType("player", false, CommandParamType.STRING),
                CommandParameter.newType("amount", false, CommandParamType.INT)
            }
        ));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("example.coins")) {
            sender.sendMessage("You do not have permission.");
            return false;
        }
        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(this.getUsage());
            return false;
        }

        Player player = sender.getServer().getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        final int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("Amount must be an integer.");
            return false;
        }

        ExamplePlugin.getInstance().coins().add(player, amount);
        return true;
    }
}
```

## After: PNX-ToolBox

With PNX-ToolBox, parsing and Bedrock overload generation come from the command definition:

```java
@CommandDefinition(
    name = "coins",
    description = "Manage player coins",
    permission = "example.coins"
)
public final class CoinsCommand extends Command {
    @Override
    protected void configure() {
        ExamplePlugin plugin = (ExamplePlugin) this.getPlugin();
        this.addSubCommand(new GiveCoinsSubCommand(plugin.coins()));
    }

    @Override
    protected Object onRun(CommandContext context) {
        context.reply("Usage: /coins give <player> <amount>");
        return null;
    }
}

@SubCommandDefinition(name = "give")
@CommandArgument(order = 0, name = "player", type = CommandArgumentType.PLAYER)
@CommandArgument(order = 1, name = "amount", type = CommandArgumentType.INTEGER)
public final class GiveCoinsSubCommand extends SubCommand {
    private final CoinsService coins;

    public GiveCoinsSubCommand(CoinsService coins) {
        this.coins = coins;
    }

    @Override
    protected Object onRun(CommandContext context) {
        Player player = context.arguments().player("player");
        int amount = context.arguments().integer("amount");
        this.coins.add(player, amount);
        return amount;
    }
}
```

PNX owns construction and registration through `@CommandDefinition`. The
toolbox owns input validation, usage messages, permission creation, nested
routing, overload generation and player synchronization. Each subcommand name
and alias is emitted as a literal Bedrock overload, so the client displays
`give` instead of a generic enum placeholder.
