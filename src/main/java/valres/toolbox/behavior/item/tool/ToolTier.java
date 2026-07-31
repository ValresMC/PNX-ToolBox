package valres.toolbox.behavior.item.tool;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.ItemTool;

import java.util.Objects;

public record ToolTier(
    int level,
    int miningSpeed,
    int baseAttackDamage,
    int durability,
    int enchantability
) {
    public static final ToolTier WOODEN = new ToolTier(
        ItemTool.TIER_WOODEN,
        2,
        4,
        ItemTool.DURABILITY_WOODEN,
        15
    );
    public static final ToolTier GOLD = new ToolTier(
        ItemTool.TIER_GOLD,
        12,
        4,
        ItemTool.DURABILITY_GOLD,
        22
    );
    public static final ToolTier STONE = new ToolTier(
        ItemTool.TIER_STONE,
        4,
        5,
        ItemTool.DURABILITY_STONE,
        5
    );
    public static final ToolTier COPPER = new ToolTier(
        ItemTool.TIER_COPPER,
        5,
        5,
        ItemTool.DURABILITY_COPPER,
        0
    );
    public static final ToolTier IRON = new ToolTier(
        ItemTool.TIER_IRON,
        6,
        6,
        ItemTool.DURABILITY_IRON,
        14
    );
    public static final ToolTier DIAMOND = new ToolTier(
        ItemTool.TIER_DIAMOND,
        8,
        7,
        ItemTool.DURABILITY_DIAMOND,
        10
    );
    public static final ToolTier NETHERITE = new ToolTier(
        ItemTool.TIER_NETHERITE,
        9,
        8,
        ItemTool.DURABILITY_NETHERITE,
        15
    );

    public ToolTier {
        if (level < 0) {
            throw new IllegalArgumentException(
                "Tool tier level must be greater than or equal to 0, got " + level
            );
        }
        if (miningSpeed <= 0) {
            throw new IllegalArgumentException(
                "Tool tier mining speed must be greater than 0, got " + miningSpeed
            );
        }
        if (baseAttackDamage <= 0 || baseAttackDamage > 32767) {
            throw new IllegalArgumentException(
                "Tool tier base attack damage must be between 1 and 32767, got "
                    + baseAttackDamage
            );
        }
        if (durability <= 0 || durability > 32767) {
            throw new IllegalArgumentException(
                "Tool tier durability must be between 1 and 32767, got " + durability
            );
        }
        if (enchantability < 0 || enchantability > 255) {
            throw new IllegalArgumentException(
                "Tool tier enchantability must be between 0 and 255, got "
                    + enchantability
            );
        }
    }

    public int attackDamageFor(@NonNull ItemTool item) {
        Objects.requireNonNull(item, "item");

        if (item.isSword()) {
            return this.baseAttackDamage;
        }
        if (item.isAxe()) {
            return reducedDamage(1);
        }
        if (item.isPickaxe() || item.isHoe()) {
            return reducedDamage(2);
        }
        if (item.isShovel() || item.isSpear()) {
            return reducedDamage(3);
        }

        return this.baseAttackDamage;
    }

    private int reducedDamage(int reduction) {
        return Math.max(1, this.baseAttackDamage - reduction);
    }
}
