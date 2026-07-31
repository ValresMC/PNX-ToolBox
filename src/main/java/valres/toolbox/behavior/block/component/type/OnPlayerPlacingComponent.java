package valres.toolbox.behavior.block.component.type;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.BlockComponent;

/** Enables the block event fired when a player places the block. */
public final class OnPlayerPlacingComponent extends BlockComponent {
	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.ON_PLAYER_PLACING;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return new CompoundTag();
	}
}
