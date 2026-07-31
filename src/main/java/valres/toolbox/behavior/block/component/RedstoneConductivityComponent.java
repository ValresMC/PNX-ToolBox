package valres.toolbox.behavior.block.component;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Controls redstone conductivity and whether wire may step down this block. */
public final class RedstoneConductivityComponent extends BlockComponent {
	private final Boolean redstoneConductor;
	private final Boolean allowsWireToStepDown;

	public RedstoneConductivityComponent() {
		this(null, null);
	}

	public RedstoneConductivityComponent(@Nullable Boolean redstoneConductor, @Nullable Boolean allowsWireToStepDown) {
		this.redstoneConductor = redstoneConductor;
		this.allowsWireToStepDown = allowsWireToStepDown;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.REDSTONE_CONDUCTIVITY;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("redstone_conductor", this.redstoneConductor, "allows_wire_to_step_down", this.allowsWireToStepDown);
	}
}
