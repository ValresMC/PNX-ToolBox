package valres.toolbox.behavior.item.tool;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.ItemTool;

import java.util.Objects;

public abstract class TieredItemTool extends ItemTool implements ToolTierProvider {
    final private ToolTier toolTier;

    protected TieredItemTool(
        @NonNull String identifier,
        @NonNull ToolTier toolTier
    ) {
        super(identifier);
        this.toolTier = Objects.requireNonNull(toolTier, "toolTier");
    }

    @Override public final @NonNull ToolTier getToolTier() {
        return this.toolTier;
    }

    @Override public final int getTier() {
        return this.toolTier.level();
    }

    @Override public final int getMaxDurability() {
        return this.toolTier.durability();
    }

    @Override public final int getEnchantAbility() {
        return this.toolTier.enchantability();
    }

    @Override public int getAttackDamage() {
        return this.toolTier.attackDamageFor(this);
    }

    @Override public Integer getDiggerSpeed(Block block) {
        Integer componentSpeed = super.getDiggerSpeed(block);
        if (componentSpeed != null || block == null) {
            return componentSpeed;
        }

        int blockToolType = block.getToolType();
        boolean correctTool = blockToolType == ItemTool.TYPE_PICKAXE && this.isPickaxe()
            || blockToolType == ItemTool.TYPE_AXE && this.isAxe()
            || blockToolType == ItemTool.TYPE_SHOVEL && this.isShovel()
            || blockToolType == ItemTool.TYPE_HOE && this.isHoe()
            || blockToolType == ItemTool.TYPE_SWORD && this.isSword()
            || blockToolType == ItemTool.TYPE_SHEARS && this.isShears();

        return correctTool ? this.toolTier.miningSpeed() : null;
    }
}
