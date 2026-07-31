package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

final public class LightDampeningComponent extends BlockComponent {
    final private int level;

    public LightDampeningComponent(int level) {
        if (level < 0 || level > 15) {
            throw new IllegalArgumentException(
                "Light dampening must be between 0 and 15"
            );
        }
        this.level = level;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.LIGHT_DAMPENING;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "lightLevel",
            (byte) this.level
        );
    }
}
