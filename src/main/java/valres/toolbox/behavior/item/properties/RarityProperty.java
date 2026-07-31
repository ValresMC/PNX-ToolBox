package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class RarityProperty extends DataDrivenItemProperty {
	public static final String COMMON = "common";
	public static final String UNCOMMON = "uncommon";
	public static final String RARE = "rare";
	public static final String EPIC = "epic";

	private final String value;

	public RarityProperty(@NonNull String value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.RARITY;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
