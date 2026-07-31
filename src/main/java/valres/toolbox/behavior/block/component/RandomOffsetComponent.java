package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.RandomOffsetAxis;

import javax.annotation.Nullable;

/**
 * Applies a deterministic random visual offset along configured axes.
 */
final public class RandomOffsetComponent extends BlockComponent {
    final private RandomOffsetAxis x;
    final private RandomOffsetAxis y;
    final private RandomOffsetAxis z;

    public RandomOffsetComponent() {
        this(null, null, null);
    }

    public RandomOffsetComponent(
        @Nullable RandomOffsetAxis x,
        @Nullable RandomOffsetAxis y,
        @Nullable RandomOffsetAxis z
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.RANDOM_OFFSET;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "x", this.x,
            "y", this.y,
            "z", this.z
        );
    }
}
