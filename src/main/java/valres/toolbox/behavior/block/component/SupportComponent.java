package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.SupportShape;

import java.util.Objects;

/**
 * Defines the support shape exposed to blocks placed against this block.
 */
final public class SupportComponent extends BlockComponent {
    final private String shape;

    public SupportComponent(@NonNull SupportShape shape) {
        this(Objects.requireNonNull(
            shape,
            "Support shape cannot be null"
        ).toString());
    }

    public SupportComponent(@NonNull String shape) {
        this.shape = Objects.requireNonNull(
            shape,
            "Support shape cannot be null"
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.SUPPORT;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound("shape", this.shape);
    }
}
