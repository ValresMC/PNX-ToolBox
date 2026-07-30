package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class UseDurationProperty extends DataDrivenItemProperty {
    final private Number value;

    public UseDurationProperty(@NonNull Number value) {
        if (!(value instanceof Byte || value instanceof Short || value instanceof Integer
            || value instanceof Long || value instanceof Float || value instanceof Double)) {
            throw new IllegalArgumentException(
                "Unsupported use duration number type: " + value.getClass().getName()
            );
        }
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.USE_DURATION;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
