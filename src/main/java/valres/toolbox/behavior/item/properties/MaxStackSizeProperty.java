package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class MaxStackSizeProperty extends DataDrivenItemProperty {
	private final int value;

	public MaxStackSizeProperty(int value) {
		if (value < 1 || value > 64) {
			throw new IllegalArgumentException("Property 'max_stack_size' must be between 1 and 64, got " + value);
		}
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.MAX_STACK_SIZE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
