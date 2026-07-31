package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;

/**
 * Defines the redstone power required by the block and its propagation behavior.
 */
final public class RedstoneConsumerComponent extends BlockComponent {
    final private int minPower;
    final private Boolean propagatesPower;

    public RedstoneConsumerComponent(int minPower) {
        this(minPower, null);
    }

    public RedstoneConsumerComponent(
        int minPower,
        @Nullable Boolean propagatesPower
    ) {
        this.minPower = minPower;
        this.propagatesPower = propagatesPower;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.REDSTONE_CONSUMER;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "min_power", this.minPower,
            "propagates_power", this.propagatesPower
        );
    }
}
