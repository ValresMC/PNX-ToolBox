package valres.toolbox.behavior.item.components;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Identifier;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class TagsComponent extends DataDrivenItemComponent {
	private final List<String> tags;

	public TagsComponent(@NonNull String... tags) {
		this(Arrays.asList(tags));
	}

	public TagsComponent(@NonNull Collection<String> tags) {
		LinkedHashSet<String> uniqueTags = new LinkedHashSet<>();

		for (String tag : tags) {
			if (tag.isBlank()) {
				throw new IllegalArgumentException("Item tag cannot be empty");
			}

			Identifier.assertValid(tag);
			uniqueTags.add(tag);
		}

		this.tags = List.copyOf(uniqueTags);
	}

	public List<String> getTags() {
		return this.tags;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.TAGS;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound(Map.of("tags", this.tags));
	}
}
