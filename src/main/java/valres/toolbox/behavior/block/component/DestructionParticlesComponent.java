package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;

/**
 * Defines the particles emitted when the block is destroyed.
 */
final public class DestructionParticlesComponent extends BlockComponent {
    final private String texture;
    final private String tintMethod;
    final private Integer particleCount;

    public DestructionParticlesComponent() {
        this(null, null, null);
    }

    public DestructionParticlesComponent(
        @Nullable String texture,
        @Nullable String tintMethod,
        @Nullable Integer particleCount
    ) {
        this.texture = texture;
        this.tintMethod = tintMethod;
        this.particleCount = particleCount;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.DESTRUCTION_PARTICLES;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "texture", this.texture,
            "tint_method", this.tintMethod,
            "particle_count", this.particleCount
        );
    }
}
