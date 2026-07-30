package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class CanDestroyInCreativeProperty extends DataDrivenItemProperty {
    final private boolean value;

    public CanDestroyInCreativeProperty() {
        this(true);
    }

    public CanDestroyInCreativeProperty(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.CAN_DESTROY_IN_CREATIVE;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
