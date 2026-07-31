package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class BlockProperty extends DataDrivenItemProperty {
	private final String blockName;

	public BlockProperty(@NonNull String blockName) {
		this.blockName = blockName;
	}

	public static @NonNull BlockProperty from(@NonNull Block block) {
		return new BlockProperty(block.getId());
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.BLOCK;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.blockName);
	}
}
