package valres.toolbox.command.argument;

import org.jspecify.annotations.NonNull;
import valres.toolbox.command.annotation.CommandArgument;
import valres.toolbox.command.exception.CommandConfigurationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

final public class Arguments {
    private Arguments() {
    }

    public static StringArgument string(String name) {
        return new StringArgument(name);
    }

    public static StringArgument optionalString(String name) {
        return new StringArgument(name, true);
    }

    public static StringArgument string(String name, String defaultValue) {
        return new StringArgument(name, defaultValue);
    }

    public static TextArgument text(String name) {
        return new TextArgument(name);
    }

    public static TextArgument optionalText(String name) {
        return new TextArgument(name, true);
    }

    public static TextArgument text(String name, String defaultValue) {
        return new TextArgument(name, defaultValue);
    }

    public static IntegerArgument integer(String name) {
        return new IntegerArgument(name);
    }

    public static IntegerArgument integer(String name, int minimum, int maximum) {
        return new IntegerArgument(name, minimum, maximum);
    }

    public static IntegerArgument optionalInteger(String name) {
        return new IntegerArgument(name, true);
    }

    public static IntegerArgument integer(String name, int defaultValue) {
        return new IntegerArgument(name, defaultValue);
    }

    public static FloatArgument decimal(String name) {
        return new FloatArgument(name);
    }

    public static FloatArgument decimal(String name, double minimum, double maximum) {
        return new FloatArgument(name, minimum, maximum);
    }

    public static FloatArgument optionalDecimal(String name) {
        return new FloatArgument(name, true);
    }

    public static FloatArgument decimal(String name, double defaultValue) {
        return new FloatArgument(name, defaultValue);
    }

    public static BooleanArgument bool(String name) {
        return new BooleanArgument(name);
    }

    public static BooleanArgument optionalBool(String name) {
        return new BooleanArgument(name, true, null);
    }

    public static BooleanArgument bool(String name, boolean defaultValue) {
        return new BooleanArgument(name, defaultValue);
    }

    public static <T> OptionsArgument<T> options(String name, Map<String, T> values) {
        return new OptionsArgument<>(name, values);
    }

    public static <T> OptionsArgument<T> optionalOptions(String name, Map<String, T> values) {
        return new OptionsArgument<>(name, values, true);
    }

    public static <E extends Enum<E>> EnumArgument<E> enumeration(String name, Class<E> type) {
        return new EnumArgument<>(name, type);
    }

    public static <E extends Enum<E>> EnumArgument<E> optionalEnumeration(String name, Class<E> type) {
        return new EnumArgument<>(name, type, true);
    }

    public static DynamicEnumArgument dynamicEnum(String name, Supplier<? extends Collection<String>> supplier) {
        return new DynamicEnumArgument(name, supplier);
    }

    public static DynamicEnumArgument optionalDynamicEnum(String name, Supplier<? extends Collection<String>> supplier) {
        return new DynamicEnumArgument(name, supplier, true);
    }

    public static PlayerArgument player(String name) {
        return new PlayerArgument(name);
    }

    public static PlayerArgument optionalPlayer(String name) {
        return new PlayerArgument(name, true);
    }

    public static TargetArgument target(String name) {
        return new TargetArgument(name);
    }

    public static TargetArgument optionalTarget(String name) {
        return new TargetArgument(name, true);
    }

    public static Vector3Argument vector3(String name) {
        return new Vector3Argument(name);
    }

    public static Vector3Argument optionalVector3(String name) {
        return new Vector3Argument(name, true);
    }

    public static BlockPositionArgument blockPosition(String name) {
        return new BlockPositionArgument(name);
    }

    public static BlockPositionArgument optionalBlockPosition(String name) {
        return new BlockPositionArgument(name, true);
    }

    public static LevelArgument level(String name) {
        return new LevelArgument(name);
    }

    public static LevelArgument optionalLevel(String name) {
        return new LevelArgument(name, true);
    }

    public static UuidArgument uuid(String name) {
        return new UuidArgument(name);
    }

    public static UuidArgument optionalUuid(String name) {
        return new UuidArgument(name, true);
    }

    public static Argument<?> fromAnnotation(@NonNull CommandArgument definition) {
        String defaultValue = definition.defaultValue();
        boolean hasDefault = !CommandArgument.NO_DEFAULT.equals(defaultValue);
        if (hasDefault && !definition.optional()) {
            throw new CommandConfigurationException(
                "Annotated argument '" + definition.name() + "' must be optional when it declares a default value"
            );
        }

        try {
            return switch (definition.type()) {
                case STRING -> hasDefault
                    ? new StringArgument(definition.name(), defaultValue)
                    : new StringArgument(definition.name(), definition.optional());
                case TEXT -> hasDefault
                    ? new TextArgument(definition.name(), defaultValue)
                    : new TextArgument(definition.name(), definition.optional());
                case INTEGER -> hasDefault
                    ? new IntegerArgument(definition.name(), Integer.parseInt(defaultValue))
                    : new IntegerArgument(definition.name(), definition.optional());
                case FLOAT -> hasDefault
                    ? new FloatArgument(definition.name(), Double.parseDouble(defaultValue))
                    : new FloatArgument(definition.name(), definition.optional());
                case BOOLEAN -> hasDefault
                    ? new BooleanArgument(definition.name(), parseBooleanDefault(definition.name(), defaultValue))
                    : new BooleanArgument(definition.name(), definition.optional(), null);
                case ENUM -> createEnumArgument(definition, hasDefault ? defaultValue : null);
                case PLAYER -> requireNoDefault(definition, new PlayerArgument(definition.name(), definition.optional()));
                case TARGET -> requireNoDefault(definition, new TargetArgument(definition.name(), definition.optional()));
                case VECTOR_3 -> requireNoDefault(definition, new Vector3Argument(definition.name(), definition.optional()));
                case BLOCK_POSITION -> requireNoDefault(definition, new BlockPositionArgument(definition.name(), definition.optional()));
                case LEVEL -> requireNoDefault(definition, new LevelArgument(definition.name(), definition.optional()));
                case UUID -> requireNoDefault(definition, new UuidArgument(definition.name(), definition.optional()));
            };
        } catch (NumberFormatException exception) {
            throw new CommandConfigurationException(
                "Invalid default value '" + defaultValue + "' for annotated argument '" + definition.name() + "'", exception
            );
        }
    }

    private static OptionsArgument<String> createEnumArgument(CommandArgument definition, String defaultValue) {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        Arrays.stream(definition.values()).forEach(value -> values.put(value, value));

        return defaultValue == null
            ? new OptionsArgument<>(definition.name(), values, definition.optional())
            : new OptionsArgument<>(definition.name(), values, defaultValue);
    }

    private static <T extends Argument<?>> T requireNoDefault(CommandArgument definition, T argument) {
        if (!CommandArgument.NO_DEFAULT.equals(definition.defaultValue())) {
            throw new CommandConfigurationException(
                "Annotated argument type " + definition.type() + " does not support a default value"
            );
        }

        return argument;
    }

    private static boolean parseBooleanDefault(String name, String value) {
        return switch (value.toLowerCase()) {
            case "true", "yes", "1" -> true;
            case "false", "no", "0" -> false;
            default -> throw new CommandConfigurationException(
                "Invalid boolean default '" + value + "' for annotated argument '" + name + "'"
            );
        };
    }
}
