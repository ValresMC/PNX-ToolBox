package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/**
 * Sets the minimum fall distance required to trigger fall-on behavior.
 */
final public class EntityFallOnComponent extends BlockComponent {
    final private double minimumFallDistance;

    public EntityFallOnComponent(double minimumFallDistance) {
        this.minimumFallDistance = minimumFallDistance;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.ENTITY_FALL_ON;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "min_fall_distance", this.minimumFallDistance
        );
    }
}
