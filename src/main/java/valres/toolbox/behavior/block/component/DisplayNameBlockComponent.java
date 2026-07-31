package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

public final class DisplayNameBlockComponent extends BlockComponent {
	private final String name;

	public DisplayNameBlockComponent(@NonNull String name) {
		this.name = name;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.DISPLAY_NAME;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("value", this.name);
	}
}
