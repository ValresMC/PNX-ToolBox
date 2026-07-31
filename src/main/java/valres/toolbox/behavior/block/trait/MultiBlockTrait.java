package valres.toolbox.behavior.block.trait;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.IntPropertyType;
import valres.toolbox.behavior.block.trait.type.BlockTraitId;
import valres.toolbox.behavior.block.trait.type.MultiBlockDirection;
import valres.toolbox.behavior.block.trait.type.MultiBlockTraitState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final public class MultiBlockTrait extends BlockTrait {
    final private String direction;
    final private int parts;
    final private IntPropertyType multiBlockPartProperty;

    public MultiBlockTrait(
        @NonNull MultiBlockDirection direction,
        int parts
    ) {
        this(
            Objects.requireNonNull(
                direction,
                "Multi-block direction cannot be null"
            ).toString(),
            parts
        );
    }

    public MultiBlockTrait(@NonNull String direction, int parts) {
        if (parts < 2 || parts > 4) {
            throw new IllegalArgumentException(
                "Block trait 'minecraft:multi_block' expects parts "
                    + "between 2 and 4, got " + parts
            );
        }

        this.direction = Objects.requireNonNull(
            direction,
            "Multi-block direction cannot be null"
        );
        this.parts = parts;
        this.multiBlockPartProperty = IntPropertyType.of(
            "minecraft:multi_block_part",
            0,
            parts - 1,
            0
        );
    }

    public @NonNull IntPropertyType getMultiBlockPartProperty() {
        return this.multiBlockPartProperty;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockTraitId.MULTI_BLOCK.toString();
    }

    @Override protected @NonNull List<?> enabledStates() {
        return List.of(MultiBlockTraitState.MULTI_BLOCK_PART);
    }

    @Override protected @NonNull Map<String, ?> values() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("direction", this.direction);
        values.put("parts", this.parts);
        return values;
    }

    @Override public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
        return List.of(this.multiBlockPartProperty);
    }
}
