package valres.toolbox.behavior.item.components;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.BlockDescriptor;
import valres.toolbox.behavior.item.components.type.DestroySpeed;

public final class DiggerComponent extends DataDrivenItemComponent {
	private final List<?> destroySpeeds;
	private final Boolean useEfficiency;

	public DiggerComponent(@NonNull Collection<?> destroySpeeds) {
		this(destroySpeeds, null);
	}

	public DiggerComponent(@NonNull Collection<?> destroySpeeds, Boolean useEfficiency) {
		this.destroySpeeds = List.copyOf(destroySpeeds);
		this.useEfficiency = useEfficiency;
	}

	public static @NonNull DestroySpeed destroySpeed(@NonNull BlockDescriptor block, int speed) {
		return new DestroySpeed(block, speed);
	}

	public static @NonNull DestroySpeed destroySpeed(@NonNull String block, int speed) {
		return new DestroySpeed(block, speed);
	}

	public static @NonNull DestroySpeed destroySpeed(@NonNull Map<String, ?> block, int speed) {
		return new DestroySpeed(block, speed);
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.DIGGER;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("use_efficiency", this.useEfficiency, "destroy_speeds", this.destroySpeeds);
	}
}
