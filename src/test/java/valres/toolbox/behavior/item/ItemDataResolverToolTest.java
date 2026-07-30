package valres.toolbox.behavior.item;

import org.junit.jupiter.api.Test;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.components.DataDrivenItemComponent;
import valres.toolbox.behavior.item.tool.TieredItemSpear;
import valres.toolbox.behavior.item.tool.TieredItemTool;
import valres.toolbox.behavior.item.tool.ToolTier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemDataResolverToolTest {
    @Test
    void createsDefaultDiggerFromVanillaTier() {
        DataDrivenItemBuilder builder = DataDrivenItemBuilder.create(
            new DiamondPickaxe()
        );

        ItemDataResolver.applyDefault(builder);

        CompoundTag digger = digger(builder);
        assertTrue(digger.getBoolean("use_efficiency"));
        assertEquals(8, firstDestroySpeed(digger).getInt("speed"));
        assertTrue(
            firstDestroySpeed(digger)
                .getCompound("block")
                .getString("tags")
                .contains("'stone'")
        );
    }

    @Test
    void customTierDrivesServerValuesAndGeneratedDigger() {
        RubyPickaxe item = new RubyPickaxe();
        DataDrivenItemBuilder builder = DataDrivenItemBuilder.create(item);

        ItemDataResolver.applyDefault(builder);

        assertEquals(8, item.getTier());
        assertEquals(10, item.getToolTier().miningSpeed());
        assertEquals(2000, item.getMaxDurability());
        assertEquals(18, item.getEnchantAbility());
        assertEquals(7, item.getAttackDamage());
        assertEquals(
            6,
            builder.getProperties()
                .get(ItemPropertyNames.DAMAGE)
                .toNBT()
                .<Integer>parseValue()
        );
        assertEquals(10, firstDestroySpeed(digger(builder)).getInt("speed"));

        Block pickaxeBlock = mock(Block.class);
        when(pickaxeBlock.getToolType()).thenReturn(ItemTool.TYPE_PICKAXE);
        assertEquals(10, item.getDiggerSpeed(pickaxeBlock));
    }

    @Test
    void derivesDamageFromTierAndToolType() {
        ToolTier tier = new ToolTier(8, 10, 9, 2000, 18);

        assertEquals(9, new TypedTool(tier, ToolKind.SWORD).getAttackDamage());
        assertEquals(8, new TypedTool(tier, ToolKind.AXE).getAttackDamage());
        assertEquals(7, new TypedTool(tier, ToolKind.PICKAXE).getAttackDamage());
        assertEquals(6, new TypedTool(tier, ToolKind.SHOVEL).getAttackDamage());
        assertEquals(7, new TypedTool(tier, ToolKind.HOE).getAttackDamage());
        assertEquals(6, new RubySpear(tier).getAttackDamage());
        assertEquals(9, new TypedTool(tier, ToolKind.GENERIC).getAttackDamage());
    }

    @Test
    void rejectsInvalidCustomTierValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new ToolTier(8, 0, 9, 2000, 18)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new ToolTier(8, 10, 0, 2000, 18)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new ToolTier(8, 10, 9, 32768, 18)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new ToolTier(8, 10, 9, 2000, 256)
        );
    }

    private static CompoundTag digger(DataDrivenItemBuilder builder) {
        DataDrivenItemComponent component = builder.getComponents().get(
            ItemComponentNames.DIGGER
        );
        assertNotNull(component);
        return assertInstanceOf(CompoundTag.class, component.toNBT());
    }

    private static CompoundTag firstDestroySpeed(CompoundTag digger) {
        return digger.getList("destroy_speeds", CompoundTag.class).get(0);
    }

    private static final class DiamondPickaxe extends ItemTool {
        private DiamondPickaxe() {
            super("test:diamond_pickaxe");
        }

        @Override public boolean isPickaxe() {
            return true;
        }

        @Override public int getTier() {
            return TIER_DIAMOND;
        }
    }

    private static final class RubyPickaxe extends TieredItemTool {
        private static final ToolTier RUBY = new ToolTier(8, 10, 9, 2000, 18);

        private RubyPickaxe() {
            super("test:ruby_pickaxe", RUBY);
        }

        @Override public boolean isPickaxe() {
            return true;
        }
    }

    private enum ToolKind {
        GENERIC,
        SWORD,
        AXE,
        PICKAXE,
        SHOVEL,
        HOE
    }

    private static final class TypedTool extends TieredItemTool {
        final private ToolKind kind;

        private TypedTool(ToolTier tier, ToolKind kind) {
            super("test:" + kind.name().toLowerCase(), tier);
            this.kind = kind;
        }

        @Override public boolean isSword() {
            return this.kind == ToolKind.SWORD;
        }

        @Override public boolean isAxe() {
            return this.kind == ToolKind.AXE;
        }

        @Override public boolean isPickaxe() {
            return this.kind == ToolKind.PICKAXE;
        }

        @Override public boolean isShovel() {
            return this.kind == ToolKind.SHOVEL;
        }

        @Override public boolean isHoe() {
            return this.kind == ToolKind.HOE;
        }
    }

    private static final class RubySpear extends TieredItemSpear {
        private RubySpear(ToolTier tier) {
            super("test:ruby_spear", tier);
        }
    }
}
