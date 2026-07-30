package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

import java.util.List;

final public class EntityPlacerComponent extends DataDrivenItemComponent {
    final private String entity;
    final private List<?> dispenseOn;
    final private List<?> useOn;

    public EntityPlacerComponent(@NonNull String entity) {
        this(entity, null, null);
    }

    public EntityPlacerComponent(String entity, List<?> dispenseOn, List<?> useOn) {
        this.entity = entity;
        this.dispenseOn = dispenseOn == null ? null : List.copyOf(dispenseOn);
        this.useOn = useOn == null ? null : List.copyOf(useOn);
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.ENTITY_PLACER;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "entity", this.entity,
            "dispense_on", this.dispenseOn,
            "use_on", this.useOn
        );
    }
}
