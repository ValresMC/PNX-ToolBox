package valres.toolbox.command;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandMap;
import org.powernukkitx.plugin.Plugin;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

final public class CommandRegistry implements AutoCloseable {
    final private Plugin plugin;
    final private CommandMap commandMap;
    final private String fallbackPrefix;
    final private Map<String, Command> commands = new LinkedHashMap<>();

    public CommandRegistry(Plugin plugin) {
        this(plugin, plugin.getName());
    }

    public CommandRegistry(Plugin plugin, String fallbackPrefix) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.commandMap = plugin.getServer().getCommandMap();
        this.fallbackPrefix = normalizePrefix(fallbackPrefix);
    }

    public void register(Command command) {
        Objects.requireNonNull(command, "Command cannot be null");
        if (command.getPlugin() != this.plugin) {
            throw new CommandConfigurationException(
                "Command '" + command.getName() + "' belongs to another plugin"
            );
        }

        String name = command.getName().toLowerCase(Locale.ROOT);
        if (this.commands.containsKey(name)) {
            throw new CommandConfigurationException(
                "Command '" + command.getName() + "' is already registered in this registry"
            );
        }

        command.initialize();
        boolean primaryLabel = this.commandMap.register(this.fallbackPrefix, command);
        this.commands.put(name, command);
        if (!primaryLabel) {
            this.plugin.getLogger().warning(
                "Command /" + name + " had a label conflict and was registered as /" + this.fallbackPrefix + ":" + name
            );
        }
        this.syncAvailableCommands();
    }

    public void register(Command... commands) {
        for (Command command : commands) {
            this.register(command);
        }
    }

    public void register(Collection<? extends Command> commands) {
        Objects.requireNonNull(commands, "Commands cannot be null");
        commands.forEach(this::register);
    }

    public Command get(String name) {
        return this.commands.get(name.toLowerCase(Locale.ROOT));
    }

    public List<Command> getCommands() {
        return List.copyOf(this.commands.values());
    }

    public boolean unregister(String name) {
        Command command = this.commands.remove(name.toLowerCase(Locale.ROOT));
        if (command == null) {
            return false;
        }

        this.commandMap.unregister(command.getLabel());
        this.syncAvailableCommands();

        return true;
    }

    public void unregisterAll() {
        if (this.commands.isEmpty()) {
            return;
        }

        String[] names = this.commands.values().stream()
            .map(Command::getLabel)
            .toArray(String[]::new);
        this.commandMap.unregister(names);
        this.commands.clear();
        this.syncAvailableCommands();
    }

    @Override
    public void close() {
        this.unregisterAll();
    }

    private void syncAvailableCommands() {
        for (Player player : this.plugin.getServer().getOnlinePlayers().values()) {
            player.syncAvailableCommands();
        }
    }

    private static String normalizePrefix(String fallbackPrefix) {
        Objects.requireNonNull(fallbackPrefix, "Fallback prefix cannot be null");
        String normalized = fallbackPrefix.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-");
        if (normalized.isBlank()) {
            throw new CommandConfigurationException(
                "Command fallback prefix cannot be blank"
            );
        }

        return normalized;
    }
}
