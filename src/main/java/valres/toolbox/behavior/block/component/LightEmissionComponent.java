package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.BlockComponentNames;

final public class LightEmissionComponent extends BlockComponent {
    final private int level;

    public LightEmissionComponent(int level) {
        if (level < 0 || level > 15) {
            throw new IllegalArgumentException(
                "Light emission must be between 0 and 15"
            );
        }
        this.level = level;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.LIGHT_EMISSION;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "emission",
            (byte) this.level
        );
    }
}
