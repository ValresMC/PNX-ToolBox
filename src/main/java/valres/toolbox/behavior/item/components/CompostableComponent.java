package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class CompostableComponent extends DataDrivenItemComponent {
    final private float compostingChance;

    public CompostableComponent(float compostingChance) {
        if (compostingChance < 0 || compostingChance > 100) {
            throw new IllegalArgumentException(
                "Component 'minecraft:compostable', value 'composting_chance' "
                    + "must be between 0 and 100, got " + compostingChance
            );
        }
        this.compostingChance = compostingChance;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.COMPOSTABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("composting_chance", this.compostingChance);
    }
}
