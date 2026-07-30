package valres.toolbox.behavior.item.components.type;

public enum EquipmentSlot {
    ARMOR_HEAD("slot.armor.head"),
    ARMOR_CHEST("slot.armor.chest"),
    ARMOR_LEGS("slot.armor.legs"),
    ARMOR_FEET("slot.armor.feet"),
    WEAPON_OFF_HAND("slot.weapon.offhand");

    final private String value;

    EquipmentSlot(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
