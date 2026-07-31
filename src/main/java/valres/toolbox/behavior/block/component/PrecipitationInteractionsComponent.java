package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.PrecipitationBehavior;

import java.util.Objects;

/**
 * Controls how the block obstructs rain and accumulates snow.
 */
final public class PrecipitationInteractionsComponent extends BlockComponent {
    final private String behavior;

    public PrecipitationInteractionsComponent() {
        this(PrecipitationBehavior.OBSTRUCT_RAIN_ACCUMULATE_SNOW);
    }

    public PrecipitationInteractionsComponent(
        @NonNull PrecipitationBehavior behavior
    ) {
        this(Objects.requireNonNull(
            behavior,
            "Precipitation behavior cannot be null"
        ).toString());
    }

    public PrecipitationInteractionsComponent(@NonNull String behavior) {
        this.behavior = Objects.requireNonNull(
            behavior,
            "Precipitation behavior cannot be null"
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.PRECIPITATION_INTERACTIONS;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "precipitation_behavior", this.behavior
        );
    }
}
