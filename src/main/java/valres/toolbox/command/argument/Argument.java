package valres.toolbox.command.argument;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

abstract public class Argument<T> {
    final private static Pattern VALID_NAME = Pattern.compile("^[a-z0-9_-]+$");

    final private String name;
    final private boolean optional;
    final private boolean hasDefault;
    final private T defaultValue;

    protected Argument(String name) {
        this(name, false, false, null);
    }

    protected Argument(String name, boolean optional) {
        this(name, optional, false, null);
    }

    protected Argument(String name, T defaultValue) {
        this(name, true, true, defaultValue);
    }

    protected Argument(String name, boolean optional, boolean hasDefault, T defaultValue) {
        Objects.requireNonNull(name, "Argument name cannot be null");

        String normalizedName = name.toLowerCase(Locale.ROOT);
        if (!VALID_NAME.matcher(normalizedName).matches()) {
            throw new CommandConfigurationException(
                "Invalid argument name '" + name + "'; use letters, numbers, underscores or hyphens"
            );
        }
        if (hasDefault && !optional) {
            throw new CommandConfigurationException(
                "Argument '" + name + "' cannot have a default value without being optional"
            );
        }

        this.name = normalizedName;
        this.optional = optional;
        this.hasDefault = hasDefault;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean hasDefault() {
        return this.hasDefault;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public int getMinimumTokens() {
        return 1;
    }

    public int getMaximumTokens() {
        return this.getMinimumTokens();
    }

    public String getTypeName() {
        return this.getClass().getSimpleName().replace("Argument", "").toLowerCase(Locale.ROOT);
    }

    public String getUsage() {
        String value = this.name + ":" + this.getTypeName();

        return this.optional ? "[" + value + "]" : "<" + value + ">";
    }

    final public T parse(CommandSender sender, List<String> tokens) {
        Objects.requireNonNull(sender, "Command sender cannot be null");
        Objects.requireNonNull(tokens, "Argument tokens cannot be null");

        int tokenCount = tokens.size();
        if (tokenCount < this.getMinimumTokens() || tokenCount > this.getMaximumTokens()) {
            throw new ArgumentParseException(
                this.name,
                String.join(" ", tokens),
                CommandMessages.format(
                    CommandMessages.ARGUMENT_TOKEN_COUNT,
                    Map.of(
                        "argument", this.name,
                        "count", this.describeTokenCount()
                    )
                )
            );
        }

        return this.parseValue(sender, String.join(" ", tokens));
    }

    final public T parse(CommandSender sender, String value) {
        return this.parse(sender, List.of(value));
    }

    abstract protected T parseValue(CommandSender sender, String value);

    abstract public CommandParameter toCommandParameter();

    private String describeTokenCount() {
        if (this.getMinimumTokens() == this.getMaximumTokens()) {
            return Integer.toString(this.getMinimumTokens());
        }
        if (this.getMaximumTokens() == Integer.MAX_VALUE) {
            return "at least " + this.getMinimumTokens();
        }

        return this.getMinimumTokens() + " to " + this.getMaximumTokens();
    }
}
