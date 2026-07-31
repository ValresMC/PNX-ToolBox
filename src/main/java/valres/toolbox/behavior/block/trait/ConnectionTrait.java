package valres.toolbox.behavior.block.trait;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.BooleanPropertyType;
import valres.toolbox.behavior.block.trait.type.BlockTraitId;
import valres.toolbox.behavior.block.trait.type.ConnectionTraitState;

import java.util.List;

final public class ConnectionTrait extends BlockTrait {
    final public static BooleanPropertyType CONNECTION_NORTH = BooleanPropertyType.of("minecraft:connection_north", false);
    final public static BooleanPropertyType CONNECTION_SOUTH = BooleanPropertyType.of("minecraft:connection_south", false);
    final public static BooleanPropertyType CONNECTION_WEST = BooleanPropertyType.of("minecraft:connection_west", false);
    final public static BooleanPropertyType CONNECTION_EAST = BooleanPropertyType.of("minecraft:connection_east", false);

    @Override public @NonNull String getIdentifier() {
        return BlockTraitId.CONNECTION.toString();
    }

    @Override protected @NonNull List<?> enabledStates() {
        return List.of(ConnectionTraitState.CARDINAL_CONNECTIONS);
    }

    @Override public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
        return List.of(
            CONNECTION_NORTH,
            CONNECTION_SOUTH,
            CONNECTION_WEST,
            CONNECTION_EAST
        );
    }
}
