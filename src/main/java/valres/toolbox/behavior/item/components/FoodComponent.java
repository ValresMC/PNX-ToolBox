package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class FoodComponent extends DataDrivenItemComponent {
	private final int nutrition;
	private final float saturationModifier;
	private final Boolean canAlwaysEat;
	private final String usingConvertsTo;

	public FoodComponent(int nutrition, float saturationModifier) {
		this(nutrition, saturationModifier, null, null);
	}

	public FoodComponent(int nutrition, float saturationModifier, Boolean canAlwaysEat, String usingConvertsTo) {
		this.nutrition = nutrition;
		this.saturationModifier = saturationModifier;
		this.canAlwaysEat = canAlwaysEat;
		this.usingConvertsTo = usingConvertsTo;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.FOOD;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("nutrition", this.nutrition, "saturation_modifier", this.saturationModifier, "can_always_eat", this.canAlwaysEat, "using_converts_to", this.usingConvertsTo);
	}
}
