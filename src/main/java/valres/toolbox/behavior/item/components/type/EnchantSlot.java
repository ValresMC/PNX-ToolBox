package valres.toolbox.behavior.item.components.type;

public enum EnchantSlot {
    ALL("all"),
    ARMOR_FEET("armor_feet"),
    ARMOR_TORSO("armor_torso"),
    ARMOR_HEAD("armor_head"),
    ARMOR_LEGS("armor_legs"),
    AXE("axe"),
    BOW("bow"),
    CARROT_STICK("carrot_stick"),
    COSMETIC_HEAD("cosmetic_head"),
    CROSSBOW("crossbow"),
    ELYTRA("elytra"),
    FISHING_ROD("fishing_rod"),
    FLINTSTEEL("flintsteel"),
    GROUP_ARMOR("g_armor"),
    GROUP_DIGGING("g_digging"),
    GROUP_TOOL("g_tool"),
    HOE("hoe"),
    MELEE_SPEAR("melee_spear"),
    NONE("none"),
    PICKAXE("pickaxe"),
    SHEARS("shears"),
    SHIELD("shield"),
    SHOVEL("shovel"),
    SPEAR("spear"),
    SWORD("sword");

    final private String value;

    EnchantSlot(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
