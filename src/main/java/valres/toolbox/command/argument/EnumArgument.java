package valres.toolbox.command.argument;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class EnumArgument<E extends Enum<E>> extends OptionsArgument<E> {
	private final Class<E> enumType;

	public EnumArgument(String name, Class<E> enumType) {
		super(name, createValues(enumType));

		this.enumType = enumType;
	}

	public EnumArgument(String name, Class<E> enumType, boolean optional) {
		super(name, createValues(enumType), optional);

		this.enumType = enumType;
	}

	public EnumArgument(String name, Class<E> enumType, E defaultValue) {
		super(name, createValues(enumType), defaultValue);

		this.enumType = enumType;
	}

	@Override public String getTypeName() {
		return this.enumType.getSimpleName().toLowerCase(Locale.ROOT);
	}

	private static <E extends Enum<E>> Map<String, E> createValues(Class<E> enumType) {
		LinkedHashMap<String, E> values = new LinkedHashMap<>();
		for (E value : enumType.getEnumConstants()) {
			values.put(value.name().toLowerCase(Locale.ROOT).replace('_', '-'), value);
		}

		return values;
	}
}
