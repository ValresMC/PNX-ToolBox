package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;

/**
 * Controls redstone conductivity and whether wire may step down this block.
 */
final public class RedstoneConductivityComponent extends BlockComponent {
    final private Boolean redstoneConductor;
    final private Boolean allowsWireToStepDown;

    public RedstoneConductivityComponent() {
        this(null, null);
    }

    public RedstoneConductivityComponent(
        @Nullable Boolean redstoneConductor,
        @Nullable Boolean allowsWireToStepDown
    ) {
        this.redstoneConductor = redstoneConductor;
        this.allowsWireToStepDown = allowsWireToStepDown;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.REDSTONE_CONDUCTIVITY;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "redstone_conductor", this.redstoneConductor,
            "allows_wire_to_step_down", this.allowsWireToStepDown
        );
    }
}
