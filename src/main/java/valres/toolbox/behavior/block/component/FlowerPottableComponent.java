package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

/**
 * Allows the block to be placed inside a flower pot.
 */
final public class FlowerPottableComponent extends BlockComponent {
    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.FLOWER_POTTABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return new CompoundTag();
    }
}
