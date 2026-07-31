package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class StorageWeightLimitComponent extends DataDrivenItemComponent {
	private final int maxWeightLimit;

	public StorageWeightLimitComponent(int maxWeightLimit) {
		this.maxWeightLimit = maxWeightLimit;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.STORAGE_WEIGHT_LIMIT;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("max_weight_limit", this.maxWeightLimit);
	}
}
