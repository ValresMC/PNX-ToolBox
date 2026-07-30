package valres.toolbox.behavior.item;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBucket;
import org.powernukkitx.item.ItemDye;
import org.powernukkitx.item.ItemPotion;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.utils.ItemArmorType;
import org.powernukkitx.utils.BlockColor;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.builder.ItemBuilder;
import valres.toolbox.behavior.item.builder.LegacyItemBuilder;
import valres.toolbox.behavior.item.components.BlockPlacerItemComponent;
import valres.toolbox.behavior.item.components.DisplayNameComponent;
import valres.toolbox.behavior.item.components.DurabilityComponent;
import valres.toolbox.behavior.item.components.DyeableComponent;
import valres.toolbox.behavior.item.components.EnchantableComponent;
import valres.toolbox.behavior.item.components.FireResistantComponent;
import valres.toolbox.behavior.item.components.FoodComponent;
import valres.toolbox.behavior.item.components.FuelComponent;
import valres.toolbox.behavior.item.components.HandEquippedComponent;
import valres.toolbox.behavior.item.components.MaxStackSizeComponent;
import valres.toolbox.behavior.item.components.StackByDataComponent;
import valres.toolbox.behavior.item.components.WearableItemComponent;
import valres.toolbox.behavior.item.components.type.EnchantSlot;
import valres.toolbox.behavior.item.properties.CanDestroyInCreativeProperty;
import valres.toolbox.behavior.item.properties.DamageProperty;
import valres.toolbox.behavior.item.properties.HandEquippedProperty;
import valres.toolbox.behavior.item.properties.IconProperty;
import valres.toolbox.behavior.item.properties.LiquidClippedProperty;
import valres.toolbox.behavior.item.properties.MaxStackSizeProperty;
import valres.toolbox.behavior.item.properties.MiningSpeedProperty;
import valres.toolbox.behavior.item.properties.ShouldDespawnProperty;
import valres.toolbox.behavior.item.properties.StackedByDataProperty;
import valres.toolbox.behavior.item.properties.UseAnimationProperty;
import valres.toolbox.behavior.item.properties.UseDurationProperty;

final public class ItemDataResolver {
    private ItemDataResolver() {
    }

    public static void applyDefault(@NonNull ItemBuilder<?> builder) {
        if (builder instanceof LegacyItemBuilder legacyBuilder) {
            applyLegacyComponents(legacyBuilder);
            return;
        }

        if (builder instanceof DataDrivenItemBuilder dataDrivenBuilder) {
            applyDataDrivenProperties(dataDrivenBuilder);
            applyDataDrivenComponents(dataDrivenBuilder);
        }
    }

    private static void applyLegacyComponents(@NonNull LegacyItemBuilder builder) {
        Item item = builder.getItem();

        builder.addComponent(new MaxStackSizeComponent(item.getMaxStackSize()));
        builder.addComponent(new StackByDataComponent(false));
        builder.addComponent(new HandEquippedComponent(item.isTool()));
    }

    private static void applyDataDrivenProperties(
        @NonNull DataDrivenItemBuilder builder
    ) {
        Item item = builder.getItem();

        builder.addProperty(new IconProperty(runtimePath(builder.getIdentifier())));
        builder.addProperty(new StackedByDataProperty(false));
        builder.addProperty(new ShouldDespawnProperty(false));
        builder.addProperty(new MaxStackSizeProperty(item.getMaxStackSize()));
        builder.addProperty(new HandEquippedProperty(item.isTool()));
        builder.addProperty(new CanDestroyInCreativeProperty(!item.isSword()));
        builder.addProperty(new LiquidClippedProperty(item instanceof ItemBucket));

        Float miningSpeed = detectMiningSpeed(item);
        if (miningSpeed != null) {
            builder.addProperty(new MiningSpeedProperty(miningSpeed));
        }

        int attackDamage = item.getAttackDamage();
        if (attackDamage > 1) {
            builder.addProperty(new DamageProperty(attackDamage - 1));
        }

        if (item.isConsumable()) {
            builder.addProperty(new UseDurationProperty(20));
        }

        builder.addProperty(new UseAnimationProperty(detectUseAnimation(item)));
    }

    private static void applyDataDrivenComponents(
        @NonNull DataDrivenItemBuilder builder
    ) {
        Item item = builder.getItem();

        builder.addComponent(new DisplayNameComponent(
            "item." + builder.getIdentifier() + ".name"
        ));

        int enchantAbility = item.getEnchantAbility();
        if (enchantAbility > 0) {
            builder.addComponent(new EnchantableComponent(
                detectEnchantSlot(item),
                enchantAbility
            ));
        }

        builder.addComponent(new FireResistantComponent(item.isLavaResistant()));

        Integer fuelTime = item.getFuelTime();
        if (fuelTime != null && fuelTime > 0) {
            builder.addComponent(new FuelComponent(fuelTime / 20f));
        }

        int maxDurability = item.getMaxDurability();
        if (item.canTakeDamage() && !item.isUnbreakable() && maxDurability > 0) {
            builder.addComponent(new DurabilityComponent(
                maxDurability,
                item.getDamageChanceMin(),
                item.getDamageChanceMax()
            ));
        }

        if (item.isArmor()) {
            builder.addComponent(new WearableItemComponent(
                detectArmorSlot(item.getWearableType()),
                item.getArmorPoints(),
                null
            ));
        }

        Block block = item.getBlock();
        if (!block.isAir()) {
            builder.addComponent(BlockPlacerItemComponent.from(block));
        }

        if (item instanceof ItemDye dye) {
            builder.addComponent(new DyeableComponent(toRgbaHex(
                dye.getDyeColor().getColor()
            )));
        }

        if (item.isEdible()) {
            int nutrition = item.getNutrition();
            float saturationModifier = nutrition > 0
                ? item.getSaturation() / (nutrition * 2f)
                : 0f;

            builder.addComponent(new FoodComponent(
                nutrition,
                saturationModifier,
                item.canAlwaysEat(),
                null
            ));
        }
    }

    private static @NonNull String runtimePath(@NonNull String identifier) {
        int separator = identifier.indexOf(':');
        return separator < 0 ? identifier : identifier.substring(separator + 1);
    }

    private static @NonNull String detectUseAnimation(@NonNull Item item) {
        if (item instanceof ItemPotion) {
            return UseAnimationProperty.DRINK;
        }
        if (item.isEdible()) {
            return UseAnimationProperty.EAT;
        }
        if (item.isBow()) {
            return UseAnimationProperty.BOW;
        }
        if (item.isCrossbow()) {
            return UseAnimationProperty.CROSSBOW;
        }
        if (item.isShield()) {
            return UseAnimationProperty.BLOCK;
        }
        if (item.isSpear()) {
            return UseAnimationProperty.SPEAR;
        }
        return UseAnimationProperty.NONE;
    }

    private static @NonNull EnchantSlot detectEnchantSlot(@NonNull Item item) {
        if (item.isSword()) {
            return EnchantSlot.SWORD;
        }
        if (item.isPickaxe()) {
            return EnchantSlot.PICKAXE;
        }
        if (item.isAxe()) {
            return EnchantSlot.AXE;
        }
        if (item.isHoe()) {
            return EnchantSlot.HOE;
        }
        if (item.isShovel()) {
            return EnchantSlot.SHOVEL;
        }
        if (item.isBow()) {
            return EnchantSlot.BOW;
        }
        if (item.isCrossbow()) {
            return EnchantSlot.CROSSBOW;
        }
        if (item.isSpear()) {
            return EnchantSlot.SPEAR;
        }
        if (item.isShield()) {
            return EnchantSlot.SHIELD;
        }
        if (item.isShears()) {
            return EnchantSlot.SHEARS;
        }
        if (item.getId().equals(Item.FISHING_ROD)) {
            return EnchantSlot.FISHING_ROD;
        }

        return switch (item.getWearableType()) {
            case HEAD -> EnchantSlot.ARMOR_HEAD;
            case CHEST -> EnchantSlot.ARMOR_TORSO;
            case LEGS -> EnchantSlot.ARMOR_LEGS;
            case FEET -> EnchantSlot.ARMOR_FEET;
            case NONE -> EnchantSlot.ALL;
        };
    }

    private static @NonNull String detectArmorSlot(@NonNull ItemArmorType slot) {
        return switch (slot) {
            case HEAD -> WearableItemComponent.ARMOR_HEAD;
            case CHEST -> WearableItemComponent.ARMOR_CHEST;
            case LEGS -> WearableItemComponent.ARMOR_LEGS;
            case FEET -> WearableItemComponent.ARMOR_FEET;
            case NONE -> "slot.armor";
        };
    }

    private static Float detectMiningSpeed(@NonNull Item item) {
        if (!item.isTool()) {
            return null;
        }

        return switch (item.getTier()) {
            case ItemTool.TIER_WOODEN -> 2f;
            case ItemTool.TIER_GOLD -> 12f;
            case ItemTool.TIER_STONE -> 4f;
            case ItemTool.TIER_COPPER -> 5f;
            case ItemTool.TIER_IRON -> 6f;
            case ItemTool.TIER_DIAMOND -> 8f;
            case ItemTool.TIER_NETHERITE -> 9f;
            default -> 1f;
        };
    }

    private static @NonNull String toRgbaHex(@NonNull BlockColor color) {
        return "%02X%02X%02X%02X".formatted(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            color.getAlpha()
        );
    }
}
