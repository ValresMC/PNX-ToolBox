package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class UseAnimationProperty extends DataDrivenItemProperty {
	public static final String EAT = "eat";
	public static final String DRINK = "drink";
	public static final String BOW = "bow";
	public static final String BLOCK = "block";
	public static final String CAMERA = "camera";
	public static final String CROSSBOW = "crossbow";
	public static final String NONE = "none";
	public static final String BRUSH = "brush";
	public static final String SPEAR = "spear";
	public static final String SPYGLASS = "spyglass";

	private final String value;

	public UseAnimationProperty(@NonNull String value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.USE_ANIMATION;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
