package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

final public class FrictionBlockComponent extends BlockComponent {
    final private double friction;

    public FrictionBlockComponent(double friction) {
        this.friction = friction;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.FRICTION;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "value",
            (float) this.friction
        );
    }
}
