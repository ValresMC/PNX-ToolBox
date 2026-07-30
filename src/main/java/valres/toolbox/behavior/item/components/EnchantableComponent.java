package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.EnchantSlot;

final public class EnchantableComponent extends DataDrivenItemComponent {
    final private String slot;
    final private int value;

    public EnchantableComponent(@NonNull EnchantSlot slot, int value) {
        this(slot.toString(), value);
    }

    public EnchantableComponent(@NonNull String slot, int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                "Component 'minecraft:enchantable', value 'value' "
                    + "must be between 0 and 255, got " + value
            );
        }
        this.slot = slot;
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.ENCHANTABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "slot", this.slot,
            "value", this.value
        );
    }
}
