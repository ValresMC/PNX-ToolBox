package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class FireResistantComponent extends DataDrivenItemComponent {
	private final boolean value;

	public FireResistantComponent() {
		this(true);
	}

	public FireResistantComponent(boolean value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.FIRE_RESISTANT;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("value", this.value);
	}
}
