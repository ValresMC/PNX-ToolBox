package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class UseModifiersComponent extends DataDrivenItemComponent {
	private final float useDuration;
	private final Boolean emitVibrations;
	private final Float movementModifier;
	private final String startSound;

	public UseModifiersComponent(float useDuration) {
		this(useDuration, null, null, null);
	}

	public UseModifiersComponent(float useDuration, Boolean emitVibrations, Float movementModifier, String startSound) {
		if (movementModifier != null && (movementModifier < 0 || movementModifier > 1)) {
			throw new IllegalArgumentException("Component 'minecraft:use_modifiers', value 'movement_modifier' " + "must be between 0.0 and 1.0, got " + movementModifier);
		}
		this.useDuration = useDuration;
		this.emitVibrations = emitVibrations;
		this.movementModifier = movementModifier;
		this.startSound = startSound;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.USE_MODIFIERS;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("use_duration", this.useDuration, "emit_vibrations", this.emitVibrations, "movement_modifier", this.movementModifier, "start_sound", this.startSound);
	}
}
