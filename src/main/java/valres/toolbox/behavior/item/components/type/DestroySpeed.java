package valres.toolbox.behavior.item.components.type;

import java.util.Map;
import org.jspecify.annotations.NonNull;

public final class DestroySpeed implements ItemComponentValue {
	private final Object block;
	private final int speed;

	public DestroySpeed(@NonNull BlockDescriptor block, int speed) {
		this((Object) block, speed);
	}

	public DestroySpeed(@NonNull String block, int speed) {
		this((Object) block, speed);
	}

	public DestroySpeed(@NonNull Map<String, ?> block, int speed) {
		this((Object) Map.copyOf(block), speed);
	}

	private DestroySpeed(Object block, int speed) {
		this.block = block;
		this.speed = speed;
	}

	@Override public @NonNull Map<String, ?> toMap() {
		return Map.of("block", this.block, "speed", this.speed);
	}
}
