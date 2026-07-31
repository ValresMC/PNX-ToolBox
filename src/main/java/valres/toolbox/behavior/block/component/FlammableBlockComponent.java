package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;

/**
 * Controls how easily the block catches fire and is destroyed by it.
 */
final public class FlammableBlockComponent extends BlockComponent {
    final private Boolean enabled;
    final private Integer catchChanceModifier;
    final private Integer destroyChanceModifier;

    public FlammableBlockComponent() {
        this(true);
    }

    public FlammableBlockComponent(boolean enabled) {
        this.enabled = enabled;
        this.catchChanceModifier = null;
        this.destroyChanceModifier = null;
    }

    public FlammableBlockComponent(
        int catchChanceModifier,
        @Nullable Integer destroyChanceModifier
    ) {
        this.enabled = null;
        this.catchChanceModifier = catchChanceModifier;
        this.destroyChanceModifier = destroyChanceModifier;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.FLAMMABLE;
    }

    @Override public @NonNull Tag toNBT() {
        if (this.enabled != null) {
            return ComponentNbtHelper.tag(this.enabled);
        }

        return ComponentNbtHelper.compound(
            "catch_chance_modifier", this.catchChanceModifier,
            "destroy_chance_modifier", this.destroyChanceModifier
        );
    }
}
