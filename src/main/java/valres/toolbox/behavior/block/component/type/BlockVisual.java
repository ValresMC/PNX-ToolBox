package valres.toolbox.behavior.block.component.type;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Combines geometry and material instances into a block visual payload.
 */
final public class BlockVisual implements BlockComponentValue {
    final private Object geometryDescription;
    final private Map<String, Object> materialInstances;

    public BlockVisual(
        @NonNull String geometry,
        @NonNull Map<String, ?> materialInstances
    ) {
        this(
            Map.of(
                "identifier",
                Objects.requireNonNull(geometry, "Geometry cannot be null")
            ),
            materialInstances
        );
    }

    public BlockVisual(
        @NonNull Map<String, ?> geometry,
        @NonNull Map<String, ?> materialInstances
    ) {
        this.geometryDescription = new LinkedHashMap<>(
            Objects.requireNonNull(geometry, "Geometry cannot be null")
        );
        this.materialInstances = new LinkedHashMap<>(
            Objects.requireNonNull(
                materialInstances,
                "Material instances cannot be null"
            )
        );
    }

    @Override public @NonNull Map<String, ?> toMap() {
        Map<String, Object> materialDescription = new LinkedHashMap<>();
        materialDescription.put("mappings", new CompoundTag());
        materialDescription.put("materials", this.materialInstances);

        return Map.of(
            "geometryDescription", this.geometryDescription,
            "materialInstancesDescription", materialDescription
        );
    }
}
