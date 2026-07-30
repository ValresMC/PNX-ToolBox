# Managers

Managers help split a large plugin into focused services with a predictable lifecycle. A manager can own a database connection, listeners, commands, caches, worlds or any other plugin subsystem.

## Lifecycle

Each manager follows four lifecycle hooks:

1. `onLoad()` — load configuration and construct internal state.
2. `onEnable()` — register listeners, tasks or commands and expose the service.
3. `beforeStop()` — stop accepting new work and prepare for shutdown.
4. `onDisable()` — release resources and clear state.

Managers are loaded and enabled by priority. Shutdown hooks run in reverse order, so finalization managers stop before critical infrastructure such as a database.

## Creating a manager

```java
@ManagerInfo(name = "database", version = "1.0.0")
@ManagerPriorityInfo(ManagerPriority.CRITICAL)
public final class DatabaseManager extends Manager {
    private Database database;

    public DatabaseManager(PluginBase plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.database = new Database(this.plugin.getDataFolder());
    }

    @Override
    protected void onEnable() {
        this.database.connect();
        this.logger.info("Database connected");
    }

    @Override
    protected void beforeStop() {
        this.database.stopAcceptingQueries();
    }

    @Override
    protected void onDisable() {
        this.database.close();
    }
}
```

`Manager` gives subclasses access to the owning `PluginBase` and its logger. `isReady()` becomes `true` while the manager is enabled.

## Connecting managers to the plugin

Create one handler, register every manager, and forward the plugin lifecycle:

```java
public final class ExamplePlugin extends PluginBase {
    private ManagersHandler managers;

    @Override
    public void onLoad() {
        this.managers = ToolBox.getInstance().createManagerHandler(this);
        this.managers.registerManager(
            new DatabaseManager(this),
            new GameplayManager(this),
            new CommandsManager(this)
        );
        this.managers.onLoad();
    }

    @Override
    public void onEnable() {
        this.managers.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.managers == null) {
            return;
        }

        this.managers.beforeStop();
        this.managers.onDisable();
        ToolBox.getInstance().removeManagerHandler(this);
    }
}
```

## Priorities

Managers with the same priority are ordered by name.

| Priority | Typical use |
| --- | --- |
| `CRITICAL` | Database, configuration, storage |
| `HIGH` | Core services needed by gameplay |
| `MEDIUM` | Regular gameplay systems; the default |
| `LOW` | Optional integrations |
| `COMMANDS` | Command registration |
| `FINALIZATION` | Work that must run after every other manager |

The `@ManagerDependencies({"database"})` annotation is available as dependency metadata through `getDependencies()`. Current lifecycle ordering is controlled by `ManagerPriority`.

## Before: manual plugin lifecycle

Without managers, the main plugin class commonly becomes responsible for every subsystem and for remembering the correct startup and shutdown order:

```java
public final class ExamplePlugin extends PluginBase {
    private Database database;
    private GameplayService gameplay;
    private CommandRegistry commands;

    @Override
    public void onLoad() {
        this.database = new Database(this.getDataFolder());
        this.gameplay = new GameplayService(this.database);
    }

    @Override
    public void onEnable() {
        this.database.connect();
        this.gameplay.enable();
        this.commands = new CommandRegistry(this);
        this.commands.register(new GameplayCommand(this, this.gameplay));
    }

    @Override
    public void onDisable() {
        if (this.commands != null) {
            this.commands.close();
        }
        if (this.gameplay != null) {
            this.gameplay.disable();
        }
        if (this.database != null) {
            this.database.close();
        }
    }
}
```

## After: focused managers

With PNX-ToolBox, each subsystem owns its cleanup and the handler applies one consistent order:

```java
this.managers.registerManager(
    new DatabaseManager(this),
    new GameplayManager(this),
    new CommandsManager(this)
);
```

The main plugin only forwards lifecycle events, while implementation details remain inside the relevant manager.

