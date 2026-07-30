package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class AllowOffHandProperty extends DataDrivenItemProperty {
    final private boolean value;

    public AllowOffHandProperty() {
        this(true);
    }

    public AllowOffHandProperty(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.ALLOW_OFF_HAND;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
