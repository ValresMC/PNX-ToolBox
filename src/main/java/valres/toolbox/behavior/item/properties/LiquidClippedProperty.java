package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class LiquidClippedProperty extends DataDrivenItemProperty {
    final private boolean value;

    public LiquidClippedProperty() {
        this(true);
    }

    public LiquidClippedProperty(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.LIQUID_CLIPPED;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
