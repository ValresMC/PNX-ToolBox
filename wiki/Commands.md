# Commands

The command framework provides nested subcommands, typed arguments, reusable rules, generated permissions, Bedrock overloads and runtime refresh.

Instead of parsing raw strings in a large `execute()` method, each command node describes its own arguments and rules. The framework validates the input before calling your handler.

## Registering commands

Keep one `CommandRegistry` in your plugin and close it when the plugin stops:

```java
public final class ExamplePlugin extends PluginBase {
    private CommandRegistry commands;

    @Override
    public void onEnable() {
        this.commands = new CommandRegistry(this);
        this.commands.register(new CoinsCommand(this, new CoinsService()));
    }

    @Override
    public void onDisable() {
        if (this.commands != null) {
            this.commands.close();
        }
    }
}
```

Registration and unregistration automatically synchronize the available commands of online players.

## Root command and subcommand

This example creates `/coins give <player> <amount>`.

### Root command

```java
@CommandInfo(
    name = "coins",
    description = "Manage player coins",
    aliases = {"money"}
)
@CommandPermission("example.coins")
public final class CoinsCommand extends Command {
    private final CoinsService coins;

    public CoinsCommand(Plugin plugin, CoinsService coins) {
        super(plugin);
        this.coins = coins;
    }

    @Override
    protected void configure() {
        this.addSubCommand(new GiveCoinsSubCommand(this.coins));
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
@CommandInfo(name = "give", description = "Give coins to a player")
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

`@CommandPermission` creates and applies a permission rule automatically. Without an explicit annotation, a root command named `coins` receives `coins.command`. Its `give` subcommand receives `coins.command.give`. When the root permission is explicitly `example.coins`, the generated subcommand permission becomes `example.coins.give`.

## Live refresh

Use a dynamic enum when completion values come from runtime data:

```java
public final class WarpCommand extends Command {
    private final WarpService warps;
    private DynamicEnumArgument warpArgument;

    public WarpCommand(Plugin plugin, WarpService warps) {
        super(plugin, "warp", "Teleport to a warp");
        this.warps = warps;
    }

    @Override
    protected void configure() {
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

A native PNX command must manually declare overloads, validate permissions, identify subcommands, parse arguments, handle errors and register itself:

```java
public final class CoinsCommand extends org.powernukkitx.command.Command
    implements PluginIdentifiableCommand {

    private final Plugin plugin;
    private final CoinsService coins;

    public CoinsCommand(Plugin plugin, CoinsService coins) {
        super("coins", "Manage player coins", "/coins give <player> <amount>");
        this.plugin = plugin;
        this.coins = coins;
        this.setPermission("example.coins");

        this.setCommandParameters(Map.of(
            "give",
            new CommandParameter[] {
                CommandParameter.newEnum("subcommand", false, new String[] {"give"}),
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

        this.coins.add(player, amount);
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
```

```java
getServer().getCommandMap().register(
    getName().toLowerCase(),
    new CoinsCommand(this, coinsService)
);
```

## After: PNX-ToolBox

With PNX-ToolBox, parsing and Bedrock overload generation come from the command definition:

```java
@CommandInfo(name = "coins", description = "Manage player coins")
@CommandPermission("example.coins")
public final class CoinsCommand extends Command {
    public CoinsCommand(Plugin plugin, CoinsService coins) {
        super(plugin);
        this.addSubCommand(new GiveCoinsSubCommand(coins));
    }

    @Override
    protected Object onRun(CommandContext context) {
        context.reply("Usage: /coins give <player> <amount>");
        return null;
    }
}

@CommandInfo(name = "give")
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

The framework now owns input validation, usage messages, permission creation, nested routing, overload generation and player synchronization.

