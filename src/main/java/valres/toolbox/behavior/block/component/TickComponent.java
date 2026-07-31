package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Schedules block tick events within the configured interval range.
 */
final public class TickComponent extends BlockComponent {
    final private int minInterval;
    final private int maxInterval;
    final private Boolean looping;

    public TickComponent(int minInterval, int maxInterval) {
        this(minInterval, maxInterval, null);
    }

    public TickComponent(
        int minInterval,
        int maxInterval,
        @Nullable Boolean looping
    ) {
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.looping = looping;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.TICK;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "interval_range", List.of(this.minInterval, this.maxInterval),
            "looping", this.looping
        );
    }
}
