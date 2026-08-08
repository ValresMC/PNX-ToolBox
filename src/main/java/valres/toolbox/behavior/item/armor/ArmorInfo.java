package valres.toolbox.behavior.item.armor;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.utils.ItemArmorType;

public record ArmorInfo(int defensePoints, int toughness, int maxDurability, int armorSlot, boolean fireProof) {
	public static final int SLOT_HEAD = 0;
	public static final int SLOT_CHEST = 1;
	public static final int SLOT_LEGS = 2;
	public static final int SLOT_FEET = 3;

	public static final ArmorInfo LEATHER_HELMET = new ArmorInfo(1, 0, 56, SLOT_HEAD, false);
	public static final ArmorInfo LEATHER_CHESTPLATE = new ArmorInfo(3, 0, 81, SLOT_CHEST, false);
	public static final ArmorInfo LEATHER_LEGGINGS = new ArmorInfo(2, 0, 76, SLOT_LEGS, false);
	public static final ArmorInfo LEATHER_BOOTS = new ArmorInfo(1, 0, 66, SLOT_FEET, false);

	public static final ArmorInfo CHAINMAIL_HELMET = new ArmorInfo(2, 0, 166, SLOT_HEAD, false);
	public static final ArmorInfo CHAINMAIL_CHESTPLATE = new ArmorInfo(5, 0, 241, SLOT_CHEST, false);
	public static final ArmorInfo CHAINMAIL_LEGGINGS = new ArmorInfo(4, 0, 226, SLOT_LEGS, false);
	public static final ArmorInfo CHAINMAIL_BOOTS = new ArmorInfo(1, 0, 196, SLOT_FEET, false);

	public static final ArmorInfo GOLDEN_HELMET = new ArmorInfo(2, 0, 78, SLOT_HEAD, false);
	public static final ArmorInfo GOLDEN_CHESTPLATE = new ArmorInfo(5, 0, 113, SLOT_CHEST, false);
	public static final ArmorInfo GOLDEN_LEGGINGS = new ArmorInfo(3, 0, 106, SLOT_LEGS, false);
	public static final ArmorInfo GOLDEN_BOOTS = new ArmorInfo(1, 0, 92, SLOT_FEET, false);

	public static final ArmorInfo COPPER_HELMET = new ArmorInfo(2, 2, 122, SLOT_HEAD, false);
	public static final ArmorInfo COPPER_CHESTPLATE = new ArmorInfo(4, 2, 177, SLOT_CHEST, false);
	public static final ArmorInfo COPPER_LEGGINGS = new ArmorInfo(3, 2, 166, SLOT_LEGS, false);
	public static final ArmorInfo COPPER_BOOTS = new ArmorInfo(1, 2, 143, SLOT_FEET, false);

	public static final ArmorInfo IRON_HELMET = new ArmorInfo(2, 0, 166, SLOT_HEAD, false);
	public static final ArmorInfo IRON_CHESTPLATE = new ArmorInfo(6, 0, 241, SLOT_CHEST, false);
	public static final ArmorInfo IRON_LEGGINGS = new ArmorInfo(5, 0, 226, SLOT_LEGS, false);
	public static final ArmorInfo IRON_BOOTS = new ArmorInfo(2, 0, 196, SLOT_FEET, false);

	public static final ArmorInfo DIAMOND_HELMET = new ArmorInfo(3, 2, 364, SLOT_HEAD, false);
	public static final ArmorInfo DIAMOND_CHESTPLATE = new ArmorInfo(8, 2, 529, SLOT_CHEST, false);
	public static final ArmorInfo DIAMOND_LEGGINGS = new ArmorInfo(6, 2, 496, SLOT_LEGS, false);
	public static final ArmorInfo DIAMOND_BOOTS = new ArmorInfo(3, 2, 430, SLOT_FEET, false);

	public static final ArmorInfo NETHERITE_HELMET = new ArmorInfo(3, 3, 407, SLOT_HEAD, true);
	public static final ArmorInfo NETHERITE_CHESTPLATE = new ArmorInfo(8, 3, 592, SLOT_CHEST, true);
	public static final ArmorInfo NETHERITE_LEGGINGS = new ArmorInfo(6, 3, 555, SLOT_LEGS, true);
	public static final ArmorInfo NETHERITE_BOOTS = new ArmorInfo(3, 3, 481, SLOT_FEET, true);

	public static final ArmorInfo TURTLE_HELMET = new ArmorInfo(2, 2, 276, SLOT_HEAD, false);

	public ArmorInfo {
		if (defensePoints < 0) {
			throw new IllegalArgumentException("Armor defense points must be greater than or equal to 0, got " + defensePoints);
		}
		if (toughness < 0) {
			throw new IllegalArgumentException("Armor toughness must be greater than or equal to 0, got " + toughness);
		}
		if (maxDurability <= 0 || maxDurability > 32767) {
			throw new IllegalArgumentException("Armor durability must be between 1 and 32767, got " + maxDurability);
		}
		if (armorSlot < SLOT_HEAD || armorSlot > SLOT_FEET) {
			throw new IllegalArgumentException("Armor slot must be SLOT_HEAD, SLOT_CHEST, SLOT_LEGS or SLOT_FEET, got " + armorSlot);
		}
	}

	public @NonNull ItemArmorType wearableType() {
		return switch (this.armorSlot) {
			case SLOT_HEAD -> ItemArmorType.HEAD;
			case SLOT_CHEST -> ItemArmorType.CHEST;
			case SLOT_LEGS -> ItemArmorType.LEGS;
			case SLOT_FEET -> ItemArmorType.FEET;
			default -> throw new IllegalStateException("Unsupported armor slot: " + this.armorSlot);
		};
	}
}
