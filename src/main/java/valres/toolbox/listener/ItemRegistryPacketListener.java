package valres.toolbox.listener;

import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.packet.ItemRegistryPacket;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.server.PacketSendEvent;
import valres.toolbox.behavior.item.CustomItemRegistry;
import valres.toolbox.behavior.item.RegisteredItemData;

final public class ItemRegistryPacketListener implements Listener {
    final private CustomItemRegistry registry;

    public ItemRegistryPacketListener(CustomItemRegistry registry) {
        this.registry = registry;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPacketSend(PacketSendEvent event) {
        if (!(event.getPacket() instanceof ItemRegistryPacket packet)) {
            return;
        }

        packet.getItemData().replaceAll(original -> {
            RegisteredItemData custom = this.registry.get(original.getIdentifier());

            return custom == null ? original : custom.toNetworkDefinition();
        });

        DefinitionRegistry<ItemDefinition> definitions = new SimpleDefinitionRegistry.Builder<ItemDefinition>().addAll(packet.getItemData()).build();

        event.getPlayer().getSession().getPeer().getCodecHelper().setItemDefinitions(definitions);
    }
}
