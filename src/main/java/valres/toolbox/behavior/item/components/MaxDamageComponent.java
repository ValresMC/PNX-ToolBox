package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class MaxDamageComponent extends LegacyItemComponent {
    final private int value;

    public MaxDamageComponent(int value) {
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.MAX_DAMAGE;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.value);
    }
}
