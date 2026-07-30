package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class UseAnimationProperty extends DataDrivenItemProperty {
    final public static String EAT = "eat";
    final public static String DRINK = "drink";
    final public static String BOW = "bow";
    final public static String BLOCK = "block";
    final public static String CAMERA = "camera";
    final public static String CROSSBOW = "crossbow";
    final public static String NONE = "none";
    final public static String BRUSH = "brush";
    final public static String SPEAR = "spear";
    final public static String SPYGLASS = "spyglass";

    final private String value;

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
