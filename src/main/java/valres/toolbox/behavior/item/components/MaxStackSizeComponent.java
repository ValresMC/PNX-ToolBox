package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class MaxStackSizeComponent extends LegacyItemComponent {
    final private int value;

    public MaxStackSizeComponent(int value) {
        if (value < 1 || value > 64) {
            throw new IllegalArgumentException(
                "Component 'minecraft:max_stack_size' must be between 1 and 64, got "
                    + value
            );
        }
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.MAX_STACK_SIZE;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
