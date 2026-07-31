package valres.toolbox.command.argument;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

public final class DynamicEnumArgument extends Argument<String> {
	private final Supplier<? extends Collection<String>> supplier;
	private final CommandEnum commandEnum;

	public DynamicEnumArgument(String name, Supplier<? extends Collection<String>> supplier) {
		this(name, supplier, false, false, null);
	}

	public DynamicEnumArgument(String name, Supplier<? extends Collection<String>> supplier, boolean optional) {
		this(name, supplier, optional, false, null);
	}

	public DynamicEnumArgument(String name, Supplier<? extends Collection<String>> supplier, String defaultValue) {
		this(name, supplier, true, true, defaultValue);
	}

	private DynamicEnumArgument(String name, @NonNull Supplier<? extends Collection<String>> supplier, boolean optional, boolean hasDefault, String defaultValue) {
		super(name, optional, hasDefault, defaultValue);

		this.supplier = supplier;
		this.commandEnum = new CommandEnum(name + "DynamicOptions", this::getValues);
	}

	@Override protected String parseValue(CommandSender sender, String value) {
		String parsed = this.normalizedValues().get(value.toLowerCase(Locale.ROOT));
		if (parsed == null) {
			throw new ArgumentParseException(this.getName(), value, CommandMessages.format(CommandMessages.ARGUMENT_DYNAMIC_ENUM, Map.of("argument", this.getName(), "value", value)));
		}

		return parsed;
	}

	@Override public String getTypeName() {
		return "enum";
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newEnum(this.getName(), this.isOptional(), this.commandEnum);
	}

	public List<String> getValues() {
		return List.copyOf(this.normalizedValues().values());
	}

	public void refresh() {
		this.commandEnum.updateSoftEnum();
	}

	private Map<String, String> normalizedValues() {
		@NonNull
		Collection<String> suppliedValues = Objects.requireNonNull(this.supplier.get(), "Dynamic enum values cannot be null");
		LinkedHashMap<String, String> values = new LinkedHashMap<>();
		for (String suppliedValue : suppliedValues) {
			if (suppliedValue == null) {
				continue;
			}

			String value = suppliedValue.trim();
			if (!value.isEmpty()) {
				values.putIfAbsent(value.toLowerCase(Locale.ROOT), value);
			}
		}

		return values;
	}
}
