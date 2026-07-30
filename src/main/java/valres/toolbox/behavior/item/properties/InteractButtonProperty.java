package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class InteractButtonProperty extends DataDrivenItemProperty {
    final private Object value;

    public InteractButtonProperty() {
        this(true);
    }

    public InteractButtonProperty(boolean value) {
        this.value = value;
    }

    public InteractButtonProperty(@NonNull String value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.INTERACT_BUTTON;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
