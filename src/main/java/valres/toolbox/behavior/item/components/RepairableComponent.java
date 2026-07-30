package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.BlockDescriptor;
import valres.toolbox.behavior.item.components.type.RepairItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

final public class RepairableComponent extends DataDrivenItemComponent {
    final private List<?> repairItems;

    public RepairableComponent(@NonNull Collection<?> repairItems) {
        this.repairItems = List.copyOf(repairItems);
    }

    public static @NonNull RepairItem repairItem(
        @NonNull BlockDescriptor item,
        @NonNull Object repairAmount
    ) {
        return RepairItem.of(item, repairAmount);
    }

    public static @NonNull RepairItem repairItem(
        @NonNull String item,
        @NonNull Object repairAmount
    ) {
        return RepairItem.of(item, repairAmount);
    }

    public static @NonNull RepairItem repairItem(
        @NonNull Map<String, ?> item,
        @NonNull Object repairAmount
    ) {
        return RepairItem.of(item, repairAmount);
    }

    public static @NonNull RepairItem repairItem(
        @NonNull Collection<?> items,
        @NonNull Object repairAmount
    ) {
        return new RepairItem(items, repairAmount);
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.REPAIRABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("repair_items", this.repairItems);
    }
}
