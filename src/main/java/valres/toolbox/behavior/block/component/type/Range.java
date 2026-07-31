package valres.toolbox.behavior.block.component.type;

import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * Represents an inclusive numeric range used by block components.
 */
public record Range(
    @NonNull Number min,
    @NonNull Number max
) implements BlockComponentValue {
    public Range {
        Objects.requireNonNull(min, "Minimum cannot be null");
        Objects.requireNonNull(max, "Maximum cannot be null");
    }

    @Override public @NonNull Map<String, ?> toMap() {
        return Map.of(
            "min", this.min,
            "max", this.max
        );
    }
}
