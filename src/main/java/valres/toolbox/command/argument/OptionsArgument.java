package valres.toolbox.command.argument;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OptionsArgument<T> extends Argument<T> {
    final private Map<String, T> values;
    final private List<String> names;
    final private CommandEnum commandEnum;

    public OptionsArgument(String name, Map<String, T> values) {
        this(name, values, false, false, null);
    }

    public OptionsArgument(String name, Map<String, T> values, boolean optional) {
        this(name, values, optional, false, null);
    }

    public OptionsArgument(String name, Map<String, T> values, T defaultValue) {
        this(name, values, true, true, defaultValue);
    }

    protected OptionsArgument(String name, Map<String, T> values, boolean optional, boolean hasDefault, T defaultValue) {
        super(name, optional, hasDefault, defaultValue);

        Objects.requireNonNull(values, "Option values cannot be null");
        LinkedHashMap<String, T> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, T> entry : values.entrySet()) {
            String option = Objects.requireNonNull(entry.getKey(), "Option name cannot be null").trim();
            if (option.isEmpty() || option.chars().anyMatch(Character::isWhitespace)) {
                throw new CommandConfigurationException(
                    "Option names for argument '" + name + "' cannot be empty or contain spaces"
                );
            }

            String key = option.toLowerCase(Locale.ROOT);
            if (normalized.containsKey(key)) {
                throw new CommandConfigurationException(
                    "Duplicate option '" + option + "' for argument '" + name + "'"
                );
            }
            normalized.put(key, Objects.requireNonNull(entry.getValue(), "Option value cannot be null"));
        }
        if (normalized.isEmpty()) {
            throw new CommandConfigurationException(
                "Option argument '" + name + "' must contain at least one value"
            );
        }
        if (hasDefault && !normalized.containsValue(defaultValue)) {
            throw new CommandConfigurationException(
                "Default value is not present in options for argument '" + name + "'"
            );
        }

        this.values = Map.copyOf(normalized);
        this.names = List.copyOf(normalized.keySet());
        this.commandEnum = new CommandEnum(name + "Options", this.names);
    }

    @Override
    protected T parseValue(CommandSender sender, String value) {
        String key = value.toLowerCase(Locale.ROOT);
        if (!this.values.containsKey(key)) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(
                    CommandMessages.ARGUMENT_OPTION,
                    Map.of(
                        "argument", this.getName(),
                        "options", String.join(", ", this.names)
                    )
                )
            );
        }

        return this.values.get(key);
    }

    @Override
    public String getTypeName() {
        return "option";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newEnum(this.getName(), this.isOptional(), this.commandEnum);
    }

    public List<String> getValues() {
        return this.names;
    }
}
