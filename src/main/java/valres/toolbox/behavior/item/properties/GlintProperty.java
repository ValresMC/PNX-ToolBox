package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

final public class GlintProperty extends DataDrivenItemProperty {
    final private boolean value;

    public GlintProperty() {
        this(true);
    }

    public GlintProperty(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.GLINT;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
