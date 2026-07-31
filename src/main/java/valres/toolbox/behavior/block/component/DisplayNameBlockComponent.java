package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

final public class DisplayNameBlockComponent extends BlockComponent {
    final private String name;

    public DisplayNameBlockComponent(@NonNull String name) {
        this.name = name;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.DISPLAY_NAME;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("value", this.name);
    }
}
