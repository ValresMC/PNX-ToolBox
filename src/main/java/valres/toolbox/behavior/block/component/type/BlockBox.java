package valres.toolbox.behavior.block.component.type;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;

public final class BlockBox implements BlockComponentValue {
	private final List<Float> origin;
	private final List<Float> size;

	public BlockBox(@NonNull List<? extends Number> origin, @NonNull List<? extends Number> size) {
		this.origin = vector(origin, "origin");
		this.size = vector(size, "size");
	}

	public static @NonNull BlockBox cube() {
		return new BlockBox(List.of(-8f, 0f, -8f), List.of(16f, 16f, 16f));
	}

	@Override public @NonNull Map<String, ?> toMap() {
		return Map.of("origin", this.origin, "size", this.size);
	}

	public @NonNull List<Float> origin() {
		return this.origin;
	}

	public @NonNull List<Float> size() {
		return this.size;
	}

	public @NonNull Map<String, ?> toCollisionMap() {
		float minimumX = 8f + this.origin.get(0);
		float minimumY = this.origin.get(1);
		float minimumZ = 8f + this.origin.get(2);

		return Map.of("minX", minimumX, "minY", minimumY, "minZ", minimumZ, "maxX", minimumX + this.size.get(0), "maxY", minimumY + this.size.get(1), "maxZ", minimumZ + this.size.get(2));
	}

	private static @NonNull List<Float> vector(@NonNull List<? extends Number> values, @NonNull String name) {
		if (values.size() != 3) {
			throw new IllegalArgumentException("Block box " + name + " must contain exactly three values");
		}

		return values.stream().map(Number::floatValue).toList();
	}
}
