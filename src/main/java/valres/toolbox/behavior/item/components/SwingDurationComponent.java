package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class SwingDurationComponent extends DataDrivenItemComponent {
    final private float value;

    public SwingDurationComponent(float value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                "Component 'minecraft:swing_duration', value 'value' "
                    + "must be at least 0.0, got " + value
            );
        }
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.SWING_DURATION;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("value", this.value);
    }
}
