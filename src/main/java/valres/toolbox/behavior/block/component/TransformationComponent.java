package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Applies translation, rotation and scale transforms to the block model.
 */
final public class TransformationComponent extends BlockComponent {
    final private List<? extends Number> translation;
    final private List<? extends Number> rotation;
    final private List<? extends Number> rotationPivot;
    final private List<? extends Number> scale;
    final private List<? extends Number> scalePivot;

    public TransformationComponent() {
        this(null, null, null, null, null);
    }

    public TransformationComponent(
        @Nullable List<? extends Number> translation,
        @Nullable List<? extends Number> rotation,
        @Nullable List<? extends Number> rotationPivot,
        @Nullable List<? extends Number> scale,
        @Nullable List<? extends Number> scalePivot
    ) {
        this.translation = copyOrNull(translation);
        this.rotation = copyOrNull(rotation);
        this.rotationPivot = copyOrNull(rotationPivot);
        this.scale = copyOrNull(scale);
        this.scalePivot = copyOrNull(scalePivot);
    }

    private static List<? extends Number> copyOrNull(
        @Nullable List<? extends Number> values
    ) {
        return values == null ? null : List.copyOf(values);
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.TRANSFORMATION;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "translation", this.translation,
            "rotation", this.rotation,
            "rotation_pivot", this.rotationPivot,
            "scale", this.scale,
            "scale_pivot", this.scalePivot
        );
    }
}
