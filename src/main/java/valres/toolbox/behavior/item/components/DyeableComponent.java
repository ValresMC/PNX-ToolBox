package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class DyeableComponent extends DataDrivenItemComponent {
	private final String defaultColor;

	public DyeableComponent() {
		this(null);
	}

	public DyeableComponent(String defaultColor) {
		this.defaultColor = defaultColor;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.DYEABLE;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("default_color", this.defaultColor);
	}
}
