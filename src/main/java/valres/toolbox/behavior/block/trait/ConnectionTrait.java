package valres.toolbox.behavior.block.trait;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.BooleanPropertyType;
import valres.toolbox.behavior.block.trait.type.BlockTraitId;
import valres.toolbox.behavior.block.trait.type.ConnectionTraitState;

public final class ConnectionTrait extends BlockTrait {
	public static final BooleanPropertyType CONNECTION_NORTH = BooleanPropertyType.of("minecraft:connection_north", false);
	public static final BooleanPropertyType CONNECTION_SOUTH = BooleanPropertyType.of("minecraft:connection_south", false);
	public static final BooleanPropertyType CONNECTION_WEST = BooleanPropertyType.of("minecraft:connection_west", false);
	public static final BooleanPropertyType CONNECTION_EAST = BooleanPropertyType.of("minecraft:connection_east", false);

	@Override public @NonNull String getIdentifier() {
		return BlockTraitId.CONNECTION.toString();
	}

	@Override protected @NonNull List<?> enabledStates() {
		return List.of(ConnectionTraitState.CARDINAL_CONNECTIONS);
	}

	@Override public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
		return List.of(CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_WEST, CONNECTION_EAST);
	}
}
