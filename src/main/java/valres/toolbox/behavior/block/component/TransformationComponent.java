package valres.toolbox.behavior.block.component;

import java.util.List;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Applies translation, rotation and scale transforms to the block model. */
public final class TransformationComponent extends BlockComponent {
	private final List<? extends Number> translation;
	private final List<? extends Number> rotation;
	private final List<? extends Number> rotationPivot;
	private final List<? extends Number> scale;
	private final List<? extends Number> scalePivot;

	public TransformationComponent() {
		this(null, null, null, null, null);
	}

	public TransformationComponent(@Nullable List<? extends Number> translation, @Nullable List<? extends Number> rotation, @Nullable List<? extends Number> rotationPivot, @Nullable List<? extends Number> scale, @Nullable List<? extends Number> scalePivot) {
		this.translation = copyOrNull(translation);
		this.rotation = copyOrNull(rotation);
		this.rotationPivot = copyOrNull(rotationPivot);
		this.scale = copyOrNull(scale);
		this.scalePivot = copyOrNull(scalePivot);
	}

	private static List<? extends Number> copyOrNull(@Nullable List<? extends Number> values) {
		return values == null ? null : List.copyOf(values);
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.TRANSFORMATION;
	}

	@Override public @NonNull Tag toNBT() {
		return new org.powernukkitx.nbt.tag.CompoundTag()
			.putInt("RX", quarterTurns(this.rotation, 0))
			.putInt("RY", quarterTurns(this.rotation, 1))
			.putInt("RZ", quarterTurns(this.rotation, 2))
			.putFloat("SX", component(this.scale, 0, 1.0f))
			.putFloat("SY", component(this.scale, 1, 1.0f))
			.putFloat("SZ", component(this.scale, 2, 1.0f))
			.putFloat("TX", component(this.translation, 0, 0.0f))
			.putFloat("TY", component(this.translation, 1, 0.0f))
			.putFloat("TZ", component(this.translation, 2, 0.0f));
	}

	private static int quarterTurns(@Nullable List<? extends Number> rotation, int axis) {
		return ((int) Math.floor(component(rotation, axis, 0.0f)) % 360) / 90;
	}

	private static float component(@Nullable List<? extends Number> values, int index, float fallback) {
		return values == null || values.size() <= index ? fallback : values.get(index).floatValue();
	}
}
