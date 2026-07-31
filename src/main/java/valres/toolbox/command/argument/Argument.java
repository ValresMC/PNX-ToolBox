package valres.toolbox.command.argument;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;

public abstract class Argument<T> {
	private static final Pattern VALID_NAME = Pattern.compile("^[a-z0-9_-]+$");

	private final String name;
	private final boolean optional;
	private final boolean hasDefault;
	private final T defaultValue;

	protected Argument(String name) {
		this(name, false, false, null);
	}

	protected Argument(String name, boolean optional) {
		this(name, optional, false, null);
	}

	protected Argument(String name, T defaultValue) {
		this(name, true, true, defaultValue);
	}

	protected Argument(@NonNull String name, boolean optional, boolean hasDefault, T defaultValue) {
		String normalizedName = name.toLowerCase(Locale.ROOT);
		if (!VALID_NAME.matcher(normalizedName).matches()) {
			throw new CommandConfigurationException("Invalid argument name '" + name + "'; use letters, numbers, underscores or hyphens");
		}
		if (hasDefault && !optional) {
			throw new CommandConfigurationException("Argument '" + name + "' cannot have a default value without being optional");
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

	public final T parse(@NonNull CommandSender sender, @NonNull List<String> tokens) {
		int tokenCount = tokens.size();
		if (tokenCount < this.getMinimumTokens() || tokenCount > this.getMaximumTokens()) {
			throw new ArgumentParseException(this.name, String.join(" ", tokens), CommandMessages.format(CommandMessages.ARGUMENT_TOKEN_COUNT, Map.of("argument", this.name, "count", this.describeTokenCount())));
		}

		return this.parseValue(sender, String.join(" ", tokens));
	}

	public final T parse(CommandSender sender, String value) {
		return this.parse(sender, List.of(value));
	}

	protected abstract T parseValue(CommandSender sender, String value);

	public abstract CommandParameter toCommandParameter();

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
