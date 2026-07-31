package valres.toolbox.behavior.block.component;

import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;

public final class ComponentNbtHelper {
	private ComponentNbtHelper() {
	}

	public static @NonNull CompoundTag compound(@NonNull Map<?, ?> values) {
		return valres.toolbox.behavior.item.components.ComponentNbtHelper.compound(values);
	}

	public static @NonNull CompoundTag compound(Object... keyValuePairs) {
		return valres.toolbox.behavior.item.components.ComponentNbtHelper.compound(keyValuePairs);
	}

	public static @NonNull Tag tag(Object value) {
		return valres.toolbox.behavior.item.components.ComponentNbtHelper.tag(value);
	}
}
