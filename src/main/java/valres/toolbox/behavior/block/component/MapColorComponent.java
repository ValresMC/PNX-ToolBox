package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import javax.annotation.Nullable;

final public class MapColorComponent extends BlockComponent {
    final private String color;
    final private String tintMethod;

    public MapColorComponent(@NonNull String color) {
        this(color, null);
    }

    public MapColorComponent(
        @NonNull String color,
        @Nullable String tintMethod
    ) {
        this.color = color;
        this.tintMethod = tintMethod;
    }

    public static @NonNull MapColorComponent rgb(
        int red,
        int green,
        int blue
    ) {
        return new MapColorComponent(
            "#%02X%02X%02X".formatted(red, green, blue)
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.MAP_COLOR;
    }

    @Override public @NonNull Tag toNBT() {
        if (this.tintMethod == null) {
            return ComponentNbtHelper.tag(this.color);
        }

        return ComponentNbtHelper.compound(
            "color", this.color,
            "tint_method", this.tintMethod
        );
    }
}
