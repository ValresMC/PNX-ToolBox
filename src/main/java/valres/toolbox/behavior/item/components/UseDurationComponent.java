package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class UseDurationComponent extends LegacyItemComponent {
	private final int value;

	public UseDurationComponent(int value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.USE_DURATION;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
