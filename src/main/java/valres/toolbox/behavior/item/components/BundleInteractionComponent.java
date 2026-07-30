package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class BundleInteractionComponent extends DataDrivenItemComponent {
    final private int numViewableSlots;

    public BundleInteractionComponent(int numViewableSlots) {
        if (numViewableSlots < 1 || numViewableSlots > 64) {
            throw new IllegalArgumentException(
                "Component 'minecraft:bundle_interaction', value 'num_viewable_slots' "
                    + "must be between 1 and 64, got " + numViewableSlots
            );
        }
        this.numViewableSlots = numViewableSlots;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.BUNDLE_INTERACTION;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("num_viewable_slots", this.numViewableSlots);
    }
}
