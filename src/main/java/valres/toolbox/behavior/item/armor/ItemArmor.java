package valres.toolbox.behavior.item.armor;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.utils.ItemArmorType;

public abstract class ItemArmor extends org.powernukkitx.item.ItemArmor {
	private final ArmorInfo armorInfo;

	protected ItemArmor(@NonNull String identifier, @NonNull ArmorInfo armorInfo) {
		super(identifier);
		this.armorInfo = Objects.requireNonNull(armorInfo, "armorInfo");
	}

	public final @NonNull ArmorInfo getArmorInfo() {
		return Objects.requireNonNull(this.armorInfo, "Armor information is not initialized");
	}

	@Override public final int getArmorPoints() {
		return this.armorInfo == null ? 0 : this.armorInfo.defensePoints();
	}

	@Override public final int getToughness() {
		return this.armorInfo == null ? 0 : this.armorInfo.toughness();
	}

	@Override public final int getMaxDurability() {
		return this.armorInfo == null ? 0 : this.armorInfo.maxDurability();
	}

	@Override public final @NonNull ItemArmorType getWearableType() {
		return this.armorInfo == null ? ItemArmorType.NONE : this.armorInfo.wearableType();
	}

	@Override public final boolean isHelmet() {
		return this.hasSlot(ArmorInfo.SLOT_HEAD);
	}

	@Override public final boolean isChestplate() {
		return this.hasSlot(ArmorInfo.SLOT_CHEST);
	}

	@Override public final boolean isLeggings() {
		return this.hasSlot(ArmorInfo.SLOT_LEGS);
	}

	@Override public final boolean isBoots() {
		return this.hasSlot(ArmorInfo.SLOT_FEET);
	}

	@Override public final boolean isLavaResistant() {
		return this.armorInfo != null && this.armorInfo.fireProof();
	}

	private boolean hasSlot(int slot) {
		return this.armorInfo != null && this.armorInfo.armorSlot() == slot;
	}
}
