package valres.toolbox.command.argument;

import java.util.Map;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;

public final class FloatArgument extends Argument<Double> {
	private final double minimum;
	private final double maximum;

	public FloatArgument(String name) {
		this(name, false, false, null, -Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public FloatArgument(String name, boolean optional) {
		this(name, optional, false, null, -Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public FloatArgument(String name, double defaultValue) {
		this(name, true, true, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public FloatArgument(String name, double minimum, double maximum) {
		this(name, false, false, null, minimum, maximum);
	}

	public FloatArgument(String name, boolean optional, double minimum, double maximum) {
		this(name, optional, false, null, minimum, maximum);
	}

	public FloatArgument(String name, double defaultValue, double minimum, double maximum) {
		this(name, true, true, defaultValue, minimum, maximum);
	}

	private FloatArgument(String name, boolean optional, boolean hasDefault, Double defaultValue, double minimum, double maximum) {
		super(name, optional, hasDefault, defaultValue);

		if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum > maximum) {
			throw new CommandConfigurationException("Invalid numeric range for argument '" + name + "'");
		}
		if (hasDefault && (!Double.isFinite(defaultValue) || defaultValue < minimum || defaultValue > maximum)) {
			throw new CommandConfigurationException("Default value is outside the allowed range for argument '" + name + "'");
		}

		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override protected Double parseValue(CommandSender sender, String value) {
		final double parsed;
		try {
			parsed = Double.parseDouble(value);
		} catch (NumberFormatException exception) {
			throw new ArgumentParseException(this.getName(), value, CommandMessages.format(CommandMessages.ARGUMENT_NUMBER, "argument", this.getName()), exception);
		}

		if (!Double.isFinite(parsed) || parsed < this.minimum || parsed > this.maximum) {
			throw new ArgumentParseException(this.getName(), value, CommandMessages.format(CommandMessages.ARGUMENT_NUMBER_RANGE, Map.of("argument", this.getName(), "minimum", this.minimum, "maximum", this.maximum)));
		}

		return parsed;
	}

	@Override public String getTypeName() {
		return "float";
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.FLOAT);
	}
}
