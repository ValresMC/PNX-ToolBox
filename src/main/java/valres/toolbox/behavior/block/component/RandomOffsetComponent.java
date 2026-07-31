package valres.toolbox.behavior.block.component;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.RandomOffsetAxis;

/** Applies a deterministic random visual offset along configured axes. */
public final class RandomOffsetComponent extends BlockComponent {
	private final RandomOffsetAxis x;
	private final RandomOffsetAxis y;
	private final RandomOffsetAxis z;

	public RandomOffsetComponent() {
		this(null, null, null);
	}

	public RandomOffsetComponent(@Nullable RandomOffsetAxis x, @Nullable RandomOffsetAxis y, @Nullable RandomOffsetAxis z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.RANDOM_OFFSET;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("x", this.x, "y", this.y, "z", this.z);
	}
}
