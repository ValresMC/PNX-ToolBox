package valres.toolbox.behavior.item.components.type;

import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

final public class BlockDescriptor implements ItemComponentValue {
    final private String name;
    final private Map<String, ?> states;
    final private String tags;

    private BlockDescriptor(String name, Map<String, ?> states, String tags) {
        this.name = name;
        this.states = states == null ? null : Map.copyOf(states);
        this.tags = tags;
    }

    public static @NonNull BlockDescriptor named(@NonNull String name) {
        return named(name, null);
    }

    public static @NonNull BlockDescriptor named(String name, Map<String, ?> states) {
        return new BlockDescriptor(name, states, null);
    }

    public static @NonNull BlockDescriptor tagged(@NonNull String query) {
        return new BlockDescriptor(null, null, query);
    }

    @Override public @NonNull Map<String, ?> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", this.name);
        values.put("states", this.states);
        values.put("tags", this.tags);
        return values;
    }
}
