package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class RecordComponent extends DataDrivenItemComponent {
	private final int comparatorSignal;
	private final float duration;
	private final String soundEvent;

	public RecordComponent(int comparatorSignal, float duration, @NonNull String soundEvent) {
		if (comparatorSignal < 1 || comparatorSignal > 13) {
			throw new IllegalArgumentException("Component 'minecraft:record', value 'comparator_signal' " + "must be between 1 and 13, got " + comparatorSignal);
		}
		this.comparatorSignal = comparatorSignal;
		this.duration = duration;
		this.soundEvent = soundEvent;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.RECORD;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("comparator_signal", this.comparatorSignal, "duration", this.duration, "sound_event", this.soundEvent);
	}
}
