package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.util.Map;

final public class IntegerArgument extends Argument<Integer> {
    final private int minimum;
    final private int maximum;

    public IntegerArgument(String name) {
        this(name, false, false, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(String name, boolean optional) {
        this(name, optional, false, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(String name, int defaultValue) {
        this(name, true, true, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(String name, int minimum, int maximum) {
        this(name, false, false, null, minimum, maximum);
    }

    public IntegerArgument(String name, boolean optional, int minimum, int maximum) {
        this(name, optional, false, null, minimum, maximum);
    }

    public IntegerArgument(String name, int defaultValue, int minimum, int maximum) {
        this(name, true, true, defaultValue, minimum, maximum);
    }

    private IntegerArgument(String name, boolean optional, boolean hasDefault, Integer defaultValue, int minimum, int maximum) {
        super(name, optional, hasDefault, defaultValue);

        if (minimum > maximum) {
            throw new CommandConfigurationException(
                "Minimum cannot exceed maximum for argument '" + name + "'"
            );
        }
        if (hasDefault && (defaultValue < minimum || defaultValue > maximum)) {
            throw new CommandConfigurationException(
                "Default value is outside the allowed range for argument '" + name + "'"
            );
        }

        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    protected Integer parseValue(CommandSender sender, String value) {
        final int parsed;
        try {
            if (!value.matches("[+-]?\\d+")) {
                throw new NumberFormatException();
            }
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(CommandMessages.ARGUMENT_INTEGER, "argument", this.getName()),
                exception
            );
        }

        if (parsed < this.minimum || parsed > this.maximum) {
            throw new ArgumentParseException(
                this.getName(),
                value,
                CommandMessages.format(
                    CommandMessages.ARGUMENT_INTEGER_RANGE,
                    Map.of(
                        "argument", this.getName(),
                        "minimum", this.minimum,
                        "maximum", this.maximum
                    )
                )
            );
        }

        return parsed;
    }

    @Override
    public String getTypeName() {
        return "int";
    }

    @Override
    public CommandParameter toCommandParameter() {
        return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.INT);
    }
}
