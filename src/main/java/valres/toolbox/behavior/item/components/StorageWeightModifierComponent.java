package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class StorageWeightModifierComponent extends DataDrivenItemComponent {
	private final int weightInStorageItem;

	public StorageWeightModifierComponent(int weightInStorageItem) {
		if (weightInStorageItem < 0 || weightInStorageItem > 64) {
			throw new IllegalArgumentException("Component 'minecraft:storage_weight_modifier', value 'weight_in_storage_item' " + "must be between 0 and 64, got " + weightInStorageItem);
		}
		this.weightInStorageItem = weightInStorageItem;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.STORAGE_WEIGHT_MODIFIER;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("weight_in_storage_item", this.weightInStorageItem);
	}
}
