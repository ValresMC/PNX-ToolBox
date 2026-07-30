package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class RarityProperty extends DataDrivenItemProperty {
    final public static String COMMON = "common";
    final public static String UNCOMMON = "uncommon";
    final public static String RARE = "rare";
    final public static String EPIC = "epic";

    final private String value;

    public RarityProperty(@NonNull String value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.RARITY;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
