package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class BlockRenderComponent extends LegacyItemComponent {
	private final String name;

	public BlockRenderComponent(@NonNull String name) {
		this.name = name;
	}

	public static @NonNull BlockRenderComponent from(@NonNull Block block) {
		return new BlockRenderComponent(block.getId());
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.BLOCK;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.name);
	}
}
