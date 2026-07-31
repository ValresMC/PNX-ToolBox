package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

/**
 * Allows another block to replace this block during placement.
 */
final public class ReplaceableComponent extends BlockComponent {
    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.REPLACEABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return new CompoundTag();
    }
}
