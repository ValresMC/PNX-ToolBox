package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

public final class FrictionBlockComponent extends BlockComponent {
	private final double friction;

	public FrictionBlockComponent(double friction) {
		this.friction = friction;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.FRICTION;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("value", (float) this.friction);
	}
}
