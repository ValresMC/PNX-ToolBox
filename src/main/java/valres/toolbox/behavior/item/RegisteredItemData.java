package valres.toolbox.behavior.item;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.powernukkitx.item.Item;

public record RegisteredItemData(
    Item item,
    Class<? extends Item> itemClass,
    int runtimeId,
    ItemVersion format,
    NbtMap componentData
) {
    public ItemDefinition toNetworkDefinition() {
        return new SimpleItemDefinition(
            this.item.getId(),
            this.runtimeId,
            this.format,
            this.format == ItemVersion.DATA_DRIVEN,
            this.componentData
        );
    }
}
