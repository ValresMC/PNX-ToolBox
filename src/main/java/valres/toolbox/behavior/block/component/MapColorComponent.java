package valres.toolbox.behavior.block.component;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

public final class MapColorComponent extends BlockComponent {
	private final String color;
	private final String tintMethod;

	public MapColorComponent(@NonNull String color) {
		this(color, null);
	}

	public MapColorComponent(@NonNull String color, @Nullable String tintMethod) {
		this.color = color;
		this.tintMethod = tintMethod;
	}

	public static @NonNull MapColorComponent rgb(int red, int green, int blue) {
		return new MapColorComponent("#%02X%02X%02X".formatted(red, green, blue));
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.MAP_COLOR;
	}

	@Override public @NonNull Tag toNBT() {
		if (this.tintMethod == null) {
			return ComponentNbtHelper.tag(this.color);
		}

		return ComponentNbtHelper.compound("color", this.color, "tint_method", this.tintMethod);
	}
}
