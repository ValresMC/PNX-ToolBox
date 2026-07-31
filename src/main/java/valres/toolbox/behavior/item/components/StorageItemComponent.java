package valres.toolbox.behavior.item.components;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class StorageItemComponent extends DataDrivenItemComponent {
	private final int maxSlots;
	private final Boolean allowNestedStorageItems;
	private final List<?> allowedItems;
	private final List<?> bannedItems;

	public StorageItemComponent(int maxSlots) {
		this(maxSlots, null, null, null);
	}

	public StorageItemComponent(int maxSlots, Boolean allowNestedStorageItems, List<?> allowedItems, List<?> bannedItems) {
		if (maxSlots < 1 || maxSlots > 64) {
			throw new IllegalArgumentException("Component 'minecraft:storage_item', value 'max_slots' " + "must be between 1 and 64, got " + maxSlots);
		}
		this.maxSlots = maxSlots;
		this.allowNestedStorageItems = allowNestedStorageItems;
		this.allowedItems = allowedItems == null ? null : List.copyOf(allowedItems);
		this.bannedItems = bannedItems == null ? null : List.copyOf(bannedItems);
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.STORAGE_ITEM;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("max_slots", this.maxSlots, "allow_nested_storage_items", this.allowNestedStorageItems, "allowed_items", this.allowedItems, "banned_items", this.bannedItems);
	}
}
