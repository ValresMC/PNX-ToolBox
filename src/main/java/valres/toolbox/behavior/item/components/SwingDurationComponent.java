package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class SwingDurationComponent extends DataDrivenItemComponent {
	private final float value;

	public SwingDurationComponent(float value) {
		if (value < 0) {
			throw new IllegalArgumentException("Component 'minecraft:swing_duration', value 'value' " + "must be at least 0.0, got " + value);
		}
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.SWING_DURATION;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("value", this.value);
	}
}
