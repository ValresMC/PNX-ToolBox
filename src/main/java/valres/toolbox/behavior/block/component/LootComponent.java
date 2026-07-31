package valres.toolbox.behavior.block.component;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Defines the loot table used when the block is destroyed. */
public final class LootComponent extends BlockComponent {
	private final String lootTable;

	public LootComponent(@NonNull String lootTable) {
		this.lootTable = Objects.requireNonNull(lootTable, "Loot table cannot be null");
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.LOOT;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.lootTable);
	}
}
