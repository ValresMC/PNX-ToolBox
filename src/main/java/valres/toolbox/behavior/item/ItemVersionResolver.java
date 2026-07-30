package valres.toolbox.behavior.item;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.Item;
import valres.toolbox.behavior.annotation.DataDrivenItem;
import valres.toolbox.behavior.annotation.LegacyItem;

import java.util.Objects;

final public class ItemVersionResolver {
    private ItemVersionResolver() {
    }

    public static ItemVersion fromItem(Item item) {
        Objects.requireNonNull(item, "item");

        return fromClass(item.getClass());
    }

    public static ItemVersion fromClass(@NonNull Class<? extends Item> itemClass) {
        boolean hasDataDriven = itemClass.isAnnotationPresent(DataDrivenItem.class);
        boolean hasLegacy = itemClass.isAnnotationPresent(LegacyItem.class);

        if (hasDataDriven && hasLegacy) {
            throw new IllegalArgumentException(
                "Item '" + itemClass.getName() + "' cannot use both " + "@DataDrivenItem and @LegacyItem annotations"
            );
        }

        if (hasDataDriven) {
            return ItemVersion.DATA_DRIVEN;
        }

        if (hasLegacy) {
            return ItemVersion.LEGACY;
        }

        return ItemVersion.NONE;
    }
}
