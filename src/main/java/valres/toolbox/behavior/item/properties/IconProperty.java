package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

import java.util.Map;

final public class IconProperty extends DataDrivenItemProperty {
    final private String icon;

    public IconProperty(@NonNull String icon) {
        this.icon = icon;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemPropertyNames.ICON;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "texture", this.icon,
            "textures", Map.of("default", this.icon)
        );
    }
}
