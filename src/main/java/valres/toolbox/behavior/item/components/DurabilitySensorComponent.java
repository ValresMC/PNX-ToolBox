package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.DurabilityThreshold;

import java.util.Collection;
import java.util.List;

final public class DurabilitySensorComponent extends DataDrivenItemComponent {
    final private List<?> thresholds;

    public DurabilitySensorComponent(@NonNull Collection<?> thresholds) {
        this.thresholds = List.copyOf(thresholds);
    }

    public static @NonNull DurabilityThreshold threshold(int durability) {
        return new DurabilityThreshold(durability);
    }

    public static @NonNull DurabilityThreshold threshold(
        int durability,
        String particleType,
        String soundEvent
    ) {
        return new DurabilityThreshold(durability, particleType, soundEvent);
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.DURABILITY_SENSOR;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("durability_thresholds", this.thresholds);
    }
}
