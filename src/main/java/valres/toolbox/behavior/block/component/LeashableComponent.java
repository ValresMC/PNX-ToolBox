package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Allows entities to be leashed to the block at an optional offset.
 */
final public class LeashableComponent extends BlockComponent {
    final private List<? extends Number> offset;

    public LeashableComponent() {
        this(null);
    }

    public LeashableComponent(@Nullable List<? extends Number> offset) {
        this.offset = offset == null ? null : List.copyOf(offset);
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.LEASHABLE;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound("offset", this.offset);
    }
}
