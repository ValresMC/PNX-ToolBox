package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class DamageProperty extends DataDrivenItemProperty {
	private final int value;

	public DamageProperty(int value) {
		if (value < 0 || value > 32767) {
			throw new IllegalArgumentException("Property 'damage' must be between 0 and 32767, got " + value);
		}
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.DAMAGE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
