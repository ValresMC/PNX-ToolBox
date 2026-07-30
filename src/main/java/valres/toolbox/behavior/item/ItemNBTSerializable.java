package valres.toolbox.behavior.item;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;

public interface ItemNBTSerializable {
    @NonNull String getIdentifier();

    @NonNull Tag toNBT();
}
