package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class HandEquippedComponent extends LegacyItemComponent {
	private final boolean value;

	public HandEquippedComponent() {
		this(true);
	}

	public HandEquippedComponent(boolean value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.HAND_EQUIPPED;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
