package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class FuelComponent extends DataDrivenItemComponent {
    final private float duration;

    public FuelComponent(float duration) {
        this.duration = duration;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.FUEL;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("duration", this.duration);
    }
}
