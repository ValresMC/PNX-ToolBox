package valres.toolbox.behavior.block.component;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Controls how easily the block catches fire and is destroyed by it. */
public final class FlammableBlockComponent extends BlockComponent {
	private final Boolean enabled;
	private final Integer catchChanceModifier;
	private final Integer destroyChanceModifier;

	public FlammableBlockComponent() {
		this(true);
	}

	public FlammableBlockComponent(boolean enabled) {
		this.enabled = enabled;
		this.catchChanceModifier = null;
		this.destroyChanceModifier = null;
	}

	public FlammableBlockComponent(int catchChanceModifier, @Nullable Integer destroyChanceModifier) {
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

		return ComponentNbtHelper.compound("catch_chance_modifier", this.catchChanceModifier, "destroy_chance_modifier", this.destroyChanceModifier);
	}
}
