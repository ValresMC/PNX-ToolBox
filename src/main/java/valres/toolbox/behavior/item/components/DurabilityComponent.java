package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

import java.util.Map;

final public class DurabilityComponent extends DataDrivenItemComponent {
    final private int maxDurability;
    final private Integer damageChanceMin;
    final private Integer damageChanceMax;

    public DurabilityComponent(int maxDurability) {
        this(maxDurability, null, null);
    }

    public DurabilityComponent(
        int maxDurability,
        Integer damageChanceMin,
        Integer damageChanceMax
    ) {
        if (maxDurability < 0 || maxDurability > 32767) {
            throw new IllegalArgumentException(
                "Component 'minecraft:durability', value 'max_durability' "
                    + "must be between 0 and 32767, got " + maxDurability
            );
        }
        this.maxDurability = maxDurability;
        this.damageChanceMin = damageChanceMin;
        this.damageChanceMax = damageChanceMax;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.DURABILITY;
    }

    @Override public @NonNull CompoundTag toNBT() {
        Map<String, Integer> damageChance = this.damageChanceMin != null
            && this.damageChanceMax != null
            ? Map.of("min", this.damageChanceMin, "max", this.damageChanceMax)
            : null;

        return ComponentNbtHelper.compound(
            "max_durability", this.maxDurability,
            "damage_chance", damageChance
        );
    }
}
