package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import java.util.Objects;

/**
 * Defines the loot table used when the block is destroyed.
 */
final public class LootComponent extends BlockComponent {
    final private String lootTable;

    public LootComponent(@NonNull String lootTable) {
        this.lootTable = Objects.requireNonNull(
            lootTable,
            "Loot table cannot be null"
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.LOOT;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.lootTable);
    }
}
