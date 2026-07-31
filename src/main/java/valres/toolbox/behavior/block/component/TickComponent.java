package valres.toolbox.behavior.block.component;

import java.util.List;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

/** Schedules block tick events within the configured interval range. */
public final class TickComponent extends BlockComponent {
	private final int minInterval;
	private final int maxInterval;
	private final Boolean looping;

	public TickComponent(int minInterval, int maxInterval) {
		this(minInterval, maxInterval, null);
	}

	public TickComponent(int minInterval, int maxInterval, @Nullable Boolean looping) {
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;
		this.looping = looping;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.TICK;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("interval_range", List.of(this.minInterval, this.maxInterval), "looping", this.looping);
	}
}
