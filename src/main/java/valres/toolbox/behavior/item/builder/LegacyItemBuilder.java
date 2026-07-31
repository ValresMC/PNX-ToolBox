package valres.toolbox.behavior.item.builder;

import org.jspecify.annotations.NonNull;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.components.LegacyItemComponent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LegacyItemBuilder extends ItemBuilder<LegacyItemBuilder> {
    final private Map<String, LegacyItemComponent> components = new LinkedHashMap<>();

    protected LegacyItemBuilder(Item item) {
        super(item);
    }

    public static LegacyItemBuilder create(Item item) {
        return new LegacyItemBuilder(item);
    }

    @Override public ItemVersion getFormat() {
        return ItemVersion.LEGACY;
    }

    @Override public CompoundTag toNBT() {
        CompoundTag components = new CompoundTag();

        for (Map.Entry<String, LegacyItemComponent> entry : this.components.entrySet()) {
            String identifier = entry.getKey();
            LegacyItemComponent component = entry.getValue();

            components.put(identifier, component.toNBT());
        }

        return new CompoundTag().putCompound(ItemBuilder.TAG_COMPONENTS, components);
    }

    public Map<String, LegacyItemComponent> getComponents() {
        return Collections.unmodifiableMap(this.components);
    }

    public void addComponent(@NonNull LegacyItemComponent component) {
        String identifier = component.getIdentifier();
        if (identifier.isBlank()) {
            throw new IllegalArgumentException(
                "Component identifier cannot be empty"
            );
        }

        this.components.put(identifier, component);
    }

    public LegacyItemBuilder removeComponent(@NonNull String componentId) {
        this.components.remove(componentId);
        return this;
    }

    public boolean hasComponent(String componentId) {
        return this.components.containsKey(componentId);
    }

    @Override
    protected LegacyItemBuilder self() {
        return this;
    }
}
