package valres.toolbox.behavior.item.components;

import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class DurabilityComponent extends DataDrivenItemComponent {
	private final int maxDurability;
	private final Integer damageChanceMin;
	private final Integer damageChanceMax;

	public DurabilityComponent(int maxDurability) {
		this(maxDurability, null, null);
	}

	public DurabilityComponent(int maxDurability, Integer damageChanceMin, Integer damageChanceMax) {
		if (maxDurability < 0 || maxDurability > 32767) {
			throw new IllegalArgumentException("Component 'minecraft:durability', value 'max_durability' " + "must be between 0 and 32767, got " + maxDurability);
		}
		this.maxDurability = maxDurability;
		this.damageChanceMin = damageChanceMin;
		this.damageChanceMax = damageChanceMax;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.DURABILITY;
	}

	@Override public @NonNull CompoundTag toNBT() {
		Map<String, Integer> damageChance = this.damageChanceMin != null && this.damageChanceMax != null ? Map.of("min", this.damageChanceMin, "max", this.damageChanceMax) : null;

		return ComponentNbtHelper.compound("max_durability", this.maxDurability, "damage_chance", damageChance);
	}
}
