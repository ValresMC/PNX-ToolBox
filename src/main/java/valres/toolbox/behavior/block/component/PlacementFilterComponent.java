package valres.toolbox.behavior.block.component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockFace;

/**
 * Restricts the faces and neighboring blocks against which placement is valid.
 */
public final class PlacementFilterComponent extends BlockComponent {
	private final List<Map<String, ?>> conditions;

	public PlacementFilterComponent(@NonNull Collection<? extends Map<String, ?>> conditions) {
		this.conditions = List.copyOf(Objects.requireNonNull(conditions, "Placement conditions cannot be null"));
	}

	public static @NonNull Map<String, ?> condition(@NonNull Collection<BlockFace> allowedFaces, @NonNull Collection<String> blockFilter) {
		return rawCondition(Objects.requireNonNull(allowedFaces, "Allowed faces cannot be null").stream().map(BlockFace::toString).toList(), Objects.requireNonNull(blockFilter, "Block filter cannot be null"));
	}

	public static @NonNull Map<String, ?> rawCondition(@NonNull Collection<String> allowedFaces, @NonNull Collection<String> blockFilter) {
		Objects.requireNonNull(allowedFaces, "Allowed faces cannot be null");
		Objects.requireNonNull(blockFilter, "Block filter cannot be null");
		Map<String, Object> condition = new LinkedHashMap<>();
		condition.put("allowed_faces", allowedFaces.isEmpty() ? null : List.copyOf(allowedFaces));
		condition.put("block_filter", blockFilter.isEmpty() ? null : List.copyOf(blockFilter));
		return condition;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.PLACEMENT_FILTER;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("conditions", this.conditions);
	}
}
