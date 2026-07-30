package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class DisplayNameComponent extends DataDrivenItemComponent {
    final private String value;

    public DisplayNameComponent(@NonNull String value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.DISPLAY_NAME;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("value", this.value);
    }
}
