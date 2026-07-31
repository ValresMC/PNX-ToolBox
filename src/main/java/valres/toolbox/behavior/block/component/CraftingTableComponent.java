package valres.toolbox.behavior.block.component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Makes the block expose a crafting table with the configured recipe tags. */
public final class CraftingTableComponent extends BlockComponent {
	private final List<String> craftingTags;
	private final String tableName;

	public CraftingTableComponent(@NonNull String... craftingTags) {
		this(List.of(Objects.requireNonNull(craftingTags, "Crafting tags cannot be null")), null);
	}

	public CraftingTableComponent(@NonNull Collection<String> craftingTags, @Nullable String tableName) {
		this.craftingTags = List.copyOf(Objects.requireNonNull(craftingTags, "Crafting tags cannot be null"));
		this.tableName = tableName;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.CRAFTING_TABLE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("table_name", this.tableName, "crafting_tags", this.craftingTags);
	}
}
