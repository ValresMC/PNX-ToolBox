package valres.toolbox.behavior.item.components;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.ItemRange;

public final class KineticWeaponComponent extends DataDrivenItemComponent {
	private final Float chargeDelay;
	private final Float hitboxMargin;
	private final Object reachRange;
	private final Object creativeReachRange;
	private final Float damageMultiplier;
	private final Float damageModifier;
	private final List<?> damageConditions;
	private final List<?> dismountConditions;
	private final List<?> knockbackConditions;

	public KineticWeaponComponent() {
		this(null, null, null, null, null, null, null, null, null);
	}

	public KineticWeaponComponent(Float chargeDelay, Float hitboxMargin, Object reachRange, Object creativeReachRange, Float damageMultiplier, Float damageModifier, List<?> damageConditions, List<?> dismountConditions, List<?> knockbackConditions) {
		requireRange(reachRange);
		requireRange(creativeReachRange);
		this.chargeDelay = chargeDelay;
		this.hitboxMargin = hitboxMargin;
		this.reachRange = reachRange;
		this.creativeReachRange = creativeReachRange;
		this.damageMultiplier = damageMultiplier;
		this.damageModifier = damageModifier;
		this.damageConditions = copy(damageConditions);
		this.dismountConditions = copy(dismountConditions);
		this.knockbackConditions = copy(knockbackConditions);
	}

	public static @NonNull ItemRange range(float min, float max) {
		return new ItemRange(min, max);
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.KINETIC_WEAPON;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("delay", this.chargeDelay, "hitbox_margin", this.hitboxMargin, "reach", this.reachRange, "creative_reach", this.creativeReachRange, "damage_multiplier", this.damageMultiplier, "damage_modifier", this.damageModifier, "damage_conditions", this.damageConditions, "dismount_conditions", this.dismountConditions, "knockback_conditions", this.knockbackConditions);
	}

	private static void requireRange(Object range) {
		if (range != null && !(range instanceof ItemRange || range instanceof Map<?, ?>)) {
			throw new IllegalArgumentException("Item range must be an ItemRange or Map");
		}
	}

	private static List<?> copy(List<?> values) {
		return values == null ? null : List.copyOf(values);
	}
}
