package valres.toolbox.command.argument;

import java.util.LinkedHashMap;
import java.util.Map;

final public class BooleanArgument extends OptionsArgument<Boolean> {
    final private static Map<String, Boolean> VALUES = createValues();

    public BooleanArgument(String name) {
        super(name, VALUES);
    }

    public BooleanArgument(String name, boolean optional, Boolean defaultValue) {
        super(name, VALUES, optional, defaultValue != null, defaultValue);
    }

    public BooleanArgument(String name, boolean defaultValue) {
        super(name, VALUES, true, true, defaultValue);
    }

    @Override public String getTypeName() {
        return "bool";
    }

    private static Map<String, Boolean> createValues() {
        LinkedHashMap<String, Boolean> values = new LinkedHashMap<>();
        values.put("true", true);
        values.put("false", false);
        values.put("yes", true);
        values.put("no", false);
        values.put("1", true);
        values.put("0", false);

        return values;
    }
}
