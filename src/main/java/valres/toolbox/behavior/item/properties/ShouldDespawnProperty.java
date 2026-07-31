package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class ShouldDespawnProperty extends DataDrivenItemProperty {
	private final boolean value;

	public ShouldDespawnProperty() {
		this(true);
	}

	public ShouldDespawnProperty(boolean value) {
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.SHOULD_DESPAWN;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.value);
	}
}
