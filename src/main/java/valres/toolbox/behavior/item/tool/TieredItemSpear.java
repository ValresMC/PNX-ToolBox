package valres.toolbox.behavior.item.tool;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.ItemSpear;

import java.util.Objects;

public abstract class TieredItemSpear extends ItemSpear
    implements ToolTierProvider {
    final private ToolTier toolTier;

    protected TieredItemSpear(
        @NonNull String identifier,
        @NonNull ToolTier toolTier
    ) {
        super(identifier, 0, 1, null);
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
}
