package valres.toolbox.behavior.block;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;

public interface BlockNBTSerializable {
    @NonNull String getIdentifier();

    @NonNull Tag toNBT();
}
