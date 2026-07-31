package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class FoilComponent extends LegacyItemComponent {
	private final boolean value;

	public FoilComponent() {
		this(true);
	}

	public FoilComponent(boolean value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.FOIL;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
