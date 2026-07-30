package valres.toolbox.behavior.item.components.type;

import org.jspecify.annotations.NonNull;

import java.util.Map;

final public class ItemRange implements ItemComponentValue {
    final private Number min;
    final private Number max;

    public ItemRange(@NonNull Number min, @NonNull Number max) {
        requireSupported(min);
        requireSupported(max);
        this.min = min;
        this.max = max;
    }

    @Override public @NonNull Map<String, ?> toMap() {
        return Map.of(
            "min", this.min,
            "max", this.max
        );
    }

    private static void requireSupported(Number value) {
        if (!(value instanceof Byte || value instanceof Short || value instanceof Integer
            || value instanceof Long || value instanceof Float || value instanceof Double)) {
            throw new IllegalArgumentException(
                "Unsupported item range number type: " + value.getClass().getName()
            );
        }
    }
}
