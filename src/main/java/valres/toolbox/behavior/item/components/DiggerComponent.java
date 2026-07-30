package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.BlockDescriptor;
import valres.toolbox.behavior.item.components.type.DestroySpeed;

import java.util.Collection;
import java.util.List;
import java.util.Map;

final public class DiggerComponent extends DataDrivenItemComponent {
    final private List<?> destroySpeeds;
    final private Boolean useEfficiency;

    public DiggerComponent(@NonNull Collection<?> destroySpeeds) {
        this(destroySpeeds, null);
    }

    public DiggerComponent(@NonNull Collection<?> destroySpeeds, Boolean useEfficiency) {
        this.destroySpeeds = List.copyOf(destroySpeeds);
        this.useEfficiency = useEfficiency;
    }

    public static @NonNull DestroySpeed destroySpeed(
        @NonNull BlockDescriptor block,
        int speed
    ) {
        return new DestroySpeed(block, speed);
    }

    public static @NonNull DestroySpeed destroySpeed(@NonNull String block, int speed) {
        return new DestroySpeed(block, speed);
    }

    public static @NonNull DestroySpeed destroySpeed(
        @NonNull Map<String, ?> block,
        int speed
    ) {
        return new DestroySpeed(block, speed);
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.DIGGER;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "use_efficiency", this.useEfficiency,
            "destroy_speeds", this.destroySpeeds
        );
    }
}
