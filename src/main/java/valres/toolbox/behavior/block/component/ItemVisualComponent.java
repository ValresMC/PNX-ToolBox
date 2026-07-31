package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockVisual;

import java.util.Objects;

/**
 * Defines the geometry and materials used when the block is shown as an item.
 */
final public class ItemVisualComponent extends BlockComponent {
    final private BlockVisual visual;

    public ItemVisualComponent(@NonNull BlockVisual visual) {
        this.visual = Objects.requireNonNull(
            visual,
            "Block visual cannot be null"
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.ITEM_VISUAL;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.visual);
    }
}
