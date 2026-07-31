package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

public class CustomComponentsComponent extends BlockComponent {
	private final boolean playerInteract;
	private final boolean playerPlacing;
	private final boolean versionOne;

	public CustomComponentsComponent(boolean playerInteract, boolean playerPlacing, boolean versionOne) {
		this.playerInteract = playerInteract;
		this.playerPlacing = playerPlacing;
		this.versionOne = versionOne;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.CUSTOM_COMPONENTS;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("hasPlayerInteract", this.playerInteract, "hasPlayerPlacing", this.playerPlacing, "isV1Component", this.versionOne);
	}
}
