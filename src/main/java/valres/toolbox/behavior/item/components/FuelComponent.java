package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class FuelComponent extends DataDrivenItemComponent {
	private final float duration;

	public FuelComponent(float duration) {
		this.duration = duration;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.FUEL;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("duration", this.duration);
	}
}
