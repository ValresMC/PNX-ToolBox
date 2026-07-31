package valres.toolbox.behavior.item.components;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class SeedComponent extends LegacyItemComponent {
	private final String cropResult;
	private final List<String> plantAt;
	private final BlockFace plantAtFace;

	public SeedComponent(@NonNull String cropResult) {
		this(cropResult, List.of(), BlockFace.UP);
	}

	public SeedComponent(@NonNull String cropResult, @NonNull Collection<String> plantAt, @NonNull BlockFace plantAtFace) {
		this.cropResult = cropResult;
		this.plantAt = List.copyOf(plantAt);
		this.plantAtFace = plantAtFace;
	}

	public static @NonNull SeedComponent fromBlocks(@NonNull Block cropResult, Block... plantAt) {
		return new SeedComponent(cropResult.getId(), Arrays.stream(plantAt).map(Block::getId).toList(), BlockFace.UP);
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.SEED;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("crop_result", normalizeBlockName(this.cropResult), "plant_at_face", this.plantAtFace, "plant_at_any_solid_surface", this.plantAt.isEmpty(), "plant_at", this.plantAt.stream().map(SeedComponent::normalizeBlockName).toList());
	}

	private static @NonNull String normalizeBlockName(@NonNull String blockName) {
		return blockName.toLowerCase(Locale.ROOT).replace("minecraft:", "").replace("grass_block", "grass").replace("air", "light_block");
	}
}
