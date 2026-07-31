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
		return ComponentNbtHelper.compound("translation", this.translation, "rotation", this.rotation, "rotation_pivot", this.rotationPivot, "scale", this.scale, "scale_pivot", this.scalePivot);
	}
}
