package valres.toolbox.behavior.block.component;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.PrecipitationBehavior;

/** Controls how the block obstructs rain and accumulates snow. */
public final class PrecipitationInteractionsComponent extends BlockComponent {
	private final String behavior;

	public PrecipitationInteractionsComponent() {
		this(PrecipitationBehavior.OBSTRUCT_RAIN_ACCUMULATE_SNOW);
	}

	public PrecipitationInteractionsComponent(@NonNull PrecipitationBehavior behavior) {
		this(Objects.requireNonNull(behavior, "Precipitation behavior cannot be null").toString());
	}

	public PrecipitationInteractionsComponent(@NonNull String behavior) {
		this.behavior = Objects.requireNonNull(behavior, "Precipitation behavior cannot be null");
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.PRECIPITATION_INTERACTIONS;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("precipitation_behavior", this.behavior);
	}
}
