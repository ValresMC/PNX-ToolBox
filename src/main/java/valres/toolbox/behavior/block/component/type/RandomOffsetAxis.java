package valres.toolbox.behavior.block.component.type;

import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * Defines the range and stepping used for random offset on one axis.
 */
public record RandomOffsetAxis(
    @NonNull Range range,
    int steps
) implements BlockComponentValue {
    public RandomOffsetAxis(@NonNull Range range) {
        this(range, 0);
    }

    public RandomOffsetAxis {
        Objects.requireNonNull(range, "Range cannot be null");
    }

    @Override public @NonNull Map<String, ?> toMap() {
        return Map.of(
            "range", this.range,
            "steps", this.steps
        );
    }
}
