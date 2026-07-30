package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class StackByDataComponent extends LegacyItemComponent {
    final private boolean value;

    public StackByDataComponent() {
        this(true);
    }

    public StackByDataComponent(boolean value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.STACK_BY_DATA;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
