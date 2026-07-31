package valres.toolbox.behavior.item.components;

import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class BlockPlacerItemComponent extends DataDrivenItemComponent {
	private final String block;
	private final Boolean replaceBlockItem;
	private final Boolean alignedPlacement;
	private final List<?> useOn;

	public BlockPlacerItemComponent(@NonNull String block) {
		this(block, null, null, null);
	}

	public BlockPlacerItemComponent(@NonNull String block, Boolean replaceBlockItem, Boolean alignedPlacement, List<?> useOn) {
		this.block = block;
		this.replaceBlockItem = replaceBlockItem;
		this.alignedPlacement = alignedPlacement;
		this.useOn = useOn == null ? null : List.copyOf(useOn);
	}

	public static @NonNull BlockPlacerItemComponent from(@NonNull Block block, Block... useOn) {
		return new BlockPlacerItemComponent(block.getId(), null, null, Arrays.stream(useOn).map(Block::getId).toList());
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.BLOCK_PLACER;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("block", this.block, "replace_block_item", this.replaceBlockItem, "aligned_placement", this.alignedPlacement, "use_on", this.useOn);
	}
}
