package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class CreativeCategoryProperty extends DataDrivenItemProperty {
	private final CreativeCategory category;

	public CreativeCategoryProperty(@NonNull CreativeCategory category) {
		this.category = category;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.CREATIVE_CATEGORY;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.category.getId());
	}
}
