package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class PiercingWeaponComponent extends DataDrivenItemComponent {
	private final int level;

	public PiercingWeaponComponent(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("Component 'minecraft:piercing_weapon', value 'level' " + "must be at least 0, got " + level);
		}
		this.level = level;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.PIERCING_WEAPON;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("level", this.level);
	}
}
