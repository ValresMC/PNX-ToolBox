package valres.toolbox.behavior.block.trait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.type.BlockPropertyType;
import valres.toolbox.behavior.block.trait.type.BlockTraitId;
import valres.toolbox.behavior.block.trait.type.PlacementPositionTraitState;

public final class PlacementPositionTrait extends BlockTrait {
	public static final BlockPropertyType<?> BLOCK_FACE = CommonBlockProperties.MINECRAFT_BLOCK_FACE;
	public static final BlockPropertyType<?> VERTICAL_HALF = CommonBlockProperties.MINECRAFT_VERTICAL_HALF;

	private final List<String> enabledStates;

	public PlacementPositionTrait(@NonNull Collection<?> enabledStates) {
		this.enabledStates = normalizeStates(enabledStates);
	}

	public static @NonNull PlacementPositionTrait blockFace() {
		return new PlacementPositionTrait(List.of(PlacementPositionTraitState.BLOCK_FACE));
	}

	public static @NonNull PlacementPositionTrait verticalHalf() {
		return new PlacementPositionTrait(List.of(PlacementPositionTraitState.VERTICAL_HALF));
	}

	public static @NonNull PlacementPositionTrait all() {
		return new PlacementPositionTrait(List.of(PlacementPositionTraitState.BLOCK_FACE, PlacementPositionTraitState.VERTICAL_HALF));
	}

	@Override public @NonNull String getIdentifier() {
		return BlockTraitId.PLACEMENT_POSITION.toString();
	}

	@Override protected @NonNull List<?> enabledStates() {
		return this.enabledStates;
	}

	@Override public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
		Map<String, BlockPropertyType<?>> properties = new LinkedHashMap<>();
		if (this.enabledStates.contains(PlacementPositionTraitState.BLOCK_FACE.toString())) {
			properties.put(BLOCK_FACE.getName(), BLOCK_FACE);
		}
		if (this.enabledStates.contains(PlacementPositionTraitState.VERTICAL_HALF.toString())) {
			properties.put(VERTICAL_HALF.getName(), VERTICAL_HALF);
		}
		return List.copyOf(properties.values());
	}

	private static @NonNull List<String> normalizeStates(@NonNull Collection<?> enabledStates) {
		Objects.requireNonNull(enabledStates, "Enabled states cannot be null");
		List<String> normalized = new ArrayList<>(enabledStates.size());

		for (Object state : enabledStates) {
			if (!(state instanceof PlacementPositionTraitState) && !(state instanceof String)) {
				throw new IllegalArgumentException("Placement-position states must be strings or " + "PlacementPositionTraitState values");
			}
			normalized.add(fullStateName(state));
		}

		return List.copyOf(normalized);
	}
}
