package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.EquipmentSlot;

public final class WearableItemComponent extends DataDrivenItemComponent {
	public static final String ARMOR_CHEST = "slot.armor.chest";
	public static final String ARMOR_FEET = "slot.armor.feet";
	public static final String ARMOR_HEAD = "slot.armor.head";
	public static final String ARMOR_LEGS = "slot.armor.legs";
	public static final String WEAPON_OFF_HAND = "slot.weapon.offhand";

	private final String slot;
	private final Integer protection;
	private final Boolean hidesPlayerLocation;

	public WearableItemComponent(@NonNull EquipmentSlot slot) {
		this(slot.toString(), null, null);
	}

	public WearableItemComponent(@NonNull String slot) {
		this(slot, null, null);
	}

	public WearableItemComponent(@NonNull EquipmentSlot slot, Integer protection, Boolean hidesPlayerLocation) {
		this(slot.toString(), protection, hidesPlayerLocation);
	}

	public WearableItemComponent(@NonNull String slot, Integer protection, Boolean hidesPlayerLocation) {
		this.slot = slot;
		this.protection = protection;
		this.hidesPlayerLocation = hidesPlayerLocation;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.WEARABLE;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("slot", this.slot, "protection", this.protection, "hides_player_location", this.hidesPlayerLocation);
	}
}
