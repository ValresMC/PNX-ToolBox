package valres.toolbox.behavior.block.component;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Defines the particles emitted when the block is destroyed. */
public final class DestructionParticlesComponent extends BlockComponent {
	private final String texture;
	private final String tintMethod;
	private final Integer particleCount;

	public DestructionParticlesComponent() {
		this(null, null, null);
	}

	public DestructionParticlesComponent(@Nullable String texture, @Nullable String tintMethod, @Nullable Integer particleCount) {
		this.texture = texture;
		this.tintMethod = tintMethod;
		this.particleCount = particleCount;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.DESTRUCTION_PARTICLES;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("texture", this.texture, "tint_method", this.tintMethod, "particle_count", this.particleCount);
	}
}
