package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.DamageCause;

import java.util.Collection;
import java.util.List;

final public class DamageAbsorptionComponent extends DataDrivenItemComponent {
    final private List<?> absorbableCauses;

    public DamageAbsorptionComponent() {
        this(List.of(DamageCause.ALL));
    }

    public DamageAbsorptionComponent(@NonNull Collection<?> absorbableCauses) {
        this.absorbableCauses = List.copyOf(absorbableCauses);
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.DAMAGE_ABSORPTION;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound("absorbable_causes", this.absorbableCauses);
    }
}
