package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class HoverTextColorProperty extends DataDrivenItemProperty {
	private final String color;

	public HoverTextColorProperty(@NonNull String color) {
		this.color = color;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.HOVER_TEXT_COLOR;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.color);
	}
}
