package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

public final class DestructibleByExplosionComponent extends BlockComponent {
	private final double resistance;

	public DestructibleByExplosionComponent(boolean enabled) {
		this(enabled ? 0 : -1);
	}

	public DestructibleByExplosionComponent(double resistance) {
		this.resistance = resistance;
	}

	public static @NonNull DestructibleByExplosionComponent resistance(double resistance) {
		return new DestructibleByExplosionComponent(resistance);
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.DESTRUCTIBLE_BY_EXPLOSION;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("explosion_resistance", (int) Math.round(this.resistance));
	}
}
