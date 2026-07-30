package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class HandEquippedProperty extends DataDrivenItemProperty {
    final private boolean value;

    public HandEquippedProperty() {
        this(true);
    }

    public HandEquippedProperty(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.HAND_EQUIPPED;
    }

    @Override public @NonNull Tag toNBT()  {
        return ComponentNbtHelper.tag(this.value);
    }
}
