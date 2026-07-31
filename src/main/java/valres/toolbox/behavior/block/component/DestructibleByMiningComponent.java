package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

public final class DestructibleByMiningComponent extends BlockComponent {
	private final double seconds;

	public DestructibleByMiningComponent(boolean enabled) {
		this(enabled ? 0 : -1);
	}

	public DestructibleByMiningComponent(double seconds) {
		this.seconds = seconds;
	}

	public static @NonNull DestructibleByMiningComponent seconds(double seconds) {
		return new DestructibleByMiningComponent(seconds);
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.DESTRUCTIBLE_BY_MINING;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("value", (float) this.seconds);
	}
}
