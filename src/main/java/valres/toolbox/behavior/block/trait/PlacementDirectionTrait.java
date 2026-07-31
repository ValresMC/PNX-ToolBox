package valres.toolbox.behavior.block.trait;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.EnumPropertyType;
import valres.toolbox.behavior.block.trait.type.BlockDescriptor;
import valres.toolbox.behavior.block.trait.type.BlockTraitId;
import valres.toolbox.behavior.block.trait.type.PlacementCorner;
import valres.toolbox.behavior.block.trait.type.PlacementDirectionTraitState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final public class PlacementDirectionTrait extends BlockTrait {
    final public static BlockPropertyType<?> CARDINAL_DIRECTION = CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
    final public static BlockPropertyType<?> FACING_DIRECTION = CommonBlockProperties.MINECRAFT_FACING_DIRECTION;
    final public static EnumPropertyType<PlacementCorner> CORNER = EnumPropertyType.of(
        "minecraft:corner",
        PlacementCorner.class,
        PlacementCorner.NONE
    );

    final private List<String> enabledStates;
    final private Integer yRotationOffset;
    final private List<Object> blocksToCornerWith;

    public PlacementDirectionTrait(@NonNull Collection<?> enabledStates) {
        this(enabledStates, null, List.of());
    }

    public PlacementDirectionTrait(
        @NonNull Collection<?> enabledStates,
        @Nullable Integer yRotationOffset,
        @NonNull Collection<?> blocksToCornerWith
    ) {
        this.enabledStates = normalizeStates(enabledStates);
        this.yRotationOffset = yRotationOffset;
        this.blocksToCornerWith = BlockDescriptor.listToValues(
            Objects.requireNonNull(
                blocksToCornerWith,
                "Blocks to corner with cannot be null"
            )
        );
    }

    public static @NonNull PlacementDirectionTrait cardinal() {
        return cardinal(null);
    }

    public static @NonNull PlacementDirectionTrait cardinal(
        @Nullable Integer yRotationOffset
    ) {
        return new PlacementDirectionTrait(
            List.of(PlacementDirectionTraitState.CARDINAL_DIRECTION),
            yRotationOffset,
            List.of()
        );
    }

    public static @NonNull PlacementDirectionTrait facing() {
        return facing(null);
    }

    public static @NonNull PlacementDirectionTrait facing(
        @Nullable Integer yRotationOffset
    ) {
        return new PlacementDirectionTrait(
            List.of(PlacementDirectionTraitState.FACING_DIRECTION),
            yRotationOffset,
            List.of()
        );
    }

    public static @NonNull PlacementDirectionTrait cornerAndCardinal() {
        return cornerAndCardinal(List.of(), null);
    }

    public static @NonNull PlacementDirectionTrait cornerAndCardinal(
        @NonNull Collection<?> blocksToCornerWith
    ) {
        return cornerAndCardinal(blocksToCornerWith, null);
    }

    public static @NonNull PlacementDirectionTrait cornerAndCardinal(
        @NonNull Collection<?> blocksToCornerWith,
        @Nullable Integer yRotationOffset
    ) {
        return new PlacementDirectionTrait(
            List.of(
                PlacementDirectionTraitState.CORNER_AND_CARDINAL_DIRECTION
            ),
            yRotationOffset,
            Objects.requireNonNull(
                blocksToCornerWith,
                "Blocks to corner with cannot be null"
            )
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockTraitId.PLACEMENT_DIRECTION.toString();
    }

    @Override protected @NonNull List<?> enabledStates() {
        return this.enabledStates;
    }

    @Override protected @NonNull Map<String, ?> values() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("y_rotation_offset", this.yRotationOffset);
        values.put(
            "blocks_to_corner_with",
            this.blocksToCornerWith.isEmpty()
                ? null
                : this.blocksToCornerWith
        );
        return values;
    }

    @Override public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
        Map<String, BlockPropertyType<?>> properties = new LinkedHashMap<>();
        if (this.enabledStates.contains(
            PlacementDirectionTraitState.CARDINAL_DIRECTION.toString()
        )) {
            properties.put(CARDINAL_DIRECTION.getName(), CARDINAL_DIRECTION);
        }
        if (this.enabledStates.contains(
            PlacementDirectionTraitState.FACING_DIRECTION.toString()
        )) {
            properties.put(FACING_DIRECTION.getName(), FACING_DIRECTION);
        }
        if (this.enabledStates.contains(
            PlacementDirectionTraitState.CORNER_AND_CARDINAL_DIRECTION.toString()
        )) {
            properties.put(CARDINAL_DIRECTION.getName(), CARDINAL_DIRECTION);
            properties.put(CORNER.getName(), CORNER);
        }
        return List.copyOf(properties.values());
    }

    private static @NonNull List<String> normalizeStates(
        @NonNull Collection<?> enabledStates
    ) {
        Objects.requireNonNull(enabledStates, "Enabled states cannot be null");
        List<String> normalized = new ArrayList<>(enabledStates.size());

        for (Object state : enabledStates) {
            if (!(state instanceof PlacementDirectionTraitState)
                && !(state instanceof String)) {
                throw new IllegalArgumentException(
                    "Placement-direction states must be strings or "
                        + "PlacementDirectionTraitState values"
                );
            }
            normalized.add(fullStateName(state));
        }

        return List.copyOf(normalized);
    }
}
