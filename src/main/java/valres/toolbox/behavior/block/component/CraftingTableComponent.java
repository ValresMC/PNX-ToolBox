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
	private static final int MAX_CRAFTING_TAGS = 64;
	private static final int MAX_CRAFTING_TAG_LENGTH = 64;

	private final List<String> craftingTags;
	private final String tableName;

	public CraftingTableComponent(@NonNull String... craftingTags) {
		this(List.of(Objects.requireNonNull(craftingTags, "Crafting tags cannot be null")), null);
	}

	public CraftingTableComponent(@NonNull Collection<String> craftingTags, @Nullable String tableName) {
		Objects.requireNonNull(craftingTags, "Crafting tags cannot be null");
		if (craftingTags.isEmpty()) {
			throw new IllegalArgumentException("At least one crafting tag is required");
		}
		if (craftingTags.size() > MAX_CRAFTING_TAGS) {
			throw new IllegalArgumentException("A crafting table cannot have more than " + MAX_CRAFTING_TAGS + " tags");
		}

		this.craftingTags = craftingTags.stream().map(CraftingTableComponent::validateCraftingTag).distinct().toList();
		if (this.craftingTags.size() != craftingTags.size()) {
			throw new IllegalArgumentException("Crafting tags cannot contain duplicates");
		}
		if (tableName != null && tableName.isBlank()) {
			throw new IllegalArgumentException("Table name cannot be empty");
		}
		this.tableName = tableName;
	}

	public @NonNull List<String> getCraftingTags() {
		return this.craftingTags;
	}

	public @Nullable String getTableName() {
		return this.tableName;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.CRAFTING_TABLE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound(
				"table_name", this.tableName,
				"grid_size", 3,
				"crafting_tags", this.craftingTags
		);
	}

	private static String validateCraftingTag(String craftingTag) {
		String normalizedCraftingTag = Objects.requireNonNull(craftingTag, "Crafting tag cannot be null").trim();
		if (normalizedCraftingTag.isEmpty()) {
			throw new IllegalArgumentException("Crafting tag cannot be empty");
		}
		if (normalizedCraftingTag.length() > MAX_CRAFTING_TAG_LENGTH) {
			throw new IllegalArgumentException("Crafting tag cannot exceed " + MAX_CRAFTING_TAG_LENGTH + " characters");
		}
		return normalizedCraftingTag;
	}
}
