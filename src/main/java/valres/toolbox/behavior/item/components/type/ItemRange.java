package valres.toolbox.behavior.item.components.type;

import java.util.Map;
import org.jspecify.annotations.NonNull;

public final class ItemRange implements ItemComponentValue {
	private final Number min;
	private final Number max;

	public ItemRange(@NonNull Number min, @NonNull Number max) {
		requireSupported(min);
		requireSupported(max);
		this.min = min;
		this.max = max;
	}

	@Override public @NonNull Map<String, ?> toMap() {
		return Map.of("min", this.min, "max", this.max);
	}

	private static void requireSupported(Number value) {
		if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double)) {
			throw new IllegalArgumentException("Unsupported item range number type: " + value.getClass().getName());
		}
	}
}
