package valres.toolbox.behavior.item;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.powernukkitx.item.Item;
import org.powernukkitx.registry.ItemRuntimeIdRegistry;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.builder.ItemBuilder;
import valres.toolbox.behavior.item.builder.LegacyItemBuilder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

final public class CustomItemRegistry {
    final private static CustomItemRegistry INSTANCE = new CustomItemRegistry();

    final private Map<String, RegisteredItemData> items = new HashMap<>();
    final private Map<Integer, String> typeIds = new HashMap<>();

    private int nextRuntimeId = 10_000;

    private CustomItemRegistry() {
    }

    public static CustomItemRegistry getInstance() {
        return INSTANCE;
    }

    public @Nullable RegisteredItemData get(String identifier) {
        return this.items.get(identifier);
    }

    public void register(Class<? extends Item> itemClass) {
        Item item;

        try {
            item = itemClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                "Item class must have a public no-argument constructor", exception
            );
        }

        String identifier = item.getId();
        this.valideIdentifier(identifier);

        switch (ItemVersionResolver.fromItem(item)) {
            case LEGACY -> this.registerLegacyItem(identifier, item);
            case DATA_DRIVEN -> this.registerDataDrivenItem(identifier, item);
            case NONE -> throw new IllegalStateException(
                "Unsupported item format"
            );
        }
    }

    public void registerLegacyItem(String identifier, Item item) {
        this.valideIdentifier(identifier);

        ItemVersion format = ItemVersionResolver.fromItem(item);
        if (format != ItemVersion.LEGACY) {
            throw new IllegalStateException(
                "Item must be a Legacy item"
            );
        }

        LegacyItemBuilder builder = LegacyItemBuilder.create(item).setRuntimeId(item.getRuntimeId());
        this.applyItemComponents(builder);
    }

    public void registerDataDrivenItem(String identifier, Item item) {
        this.valideIdentifier(identifier);

        ItemVersion format = ItemVersionResolver.fromItem(item);
        if (format != ItemVersion.DATA_DRIVEN) {
            throw new IllegalStateException(
                "Item must be a Data-Driven item"
            );
        }

        DataDrivenItemBuilder builder = DataDrivenItemBuilder.create(item).setRuntimeId(item.getRuntimeId());
        this.applyItemComponents(builder);
    }

    public void applyItemComponents(ItemBuilder<?> builder) {
        Item item = builder.getItem();

        ItemDataResolver.applyDefault(builder);

        if (builder instanceof DataDrivenItemBuilder builder_) {
            if (item instanceof LegacyExtraComponentsInterface) {
                throw new IllegalStateException(
                    "Data-Driven item cannot define legacy components"
                );
            }

            if (item instanceof DataDrivenExtraComponentsInterface item_) {
                (item_).defineDataDrivenComponent(builder_);
            }
        } else if (builder instanceof LegacyItemBuilder builder_) {
            if (item instanceof DataDrivenExtraComponentsInterface) {
                throw new IllegalStateException(
                    "Legacy item cannot define data-driven components"
                );
            }

            if (item instanceof LegacyExtraComponentsInterface item_) {
                item_.defineLegacyComponent(builder_);
            }
        }
    }

    private void deepRegister(ItemBuilder<?> builder) {
        Item item = builder.getItem();
        String identifier = builder.getIdentifier();
        int runtimeId = builder.getRuntimeId();

        if (this.items.containsKey(identifier)) {
            throw new IllegalStateException(
                "Item '" + identifier + "' is already registered"
            );
        }

        if (this.typeIds.containsKey(runtimeId)) {
            throw new IllegalStateException(
                "Runtime ID '" + runtimeId + "' is already registered"
            );
        }

        NbtMap componentData = builder.toNBT().toNetwork();

        try {
            Registries.ITEM.register(identifier, item.getClass());

            Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(
                new ItemRuntimeIdRegistry.RuntimeEntry(identifier, runtimeId, builder.getFormat() == ItemVersion.DATA_DRIVEN)
            );
        } catch (RegisterException exception) {
            throw new IllegalStateException(
                "Cannot register item '" + identifier + "'", exception
            );
        }

        RegisteredItemData data = new RegisteredItemData(item, item.getClass(), runtimeId, builder.getFormat(), componentData);

        this.items.put(identifier, data);
        this.typeIds.put(runtimeId, identifier);

        Registries.CREATIVE.addCreativeItem(item);
    }

    private void valideIdentifier(String identifier) {
        if (identifier.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Identifier cannot be empty"
            );
        }

        if (!identifier.contains(":")) {
            throw new IllegalArgumentException(
                "Identifier must be namespaced"
            );
        }

        if (identifier.startsWith("minecraft:")) {
            throw new IllegalArgumentException(
                "Identifier cannot use the minecraft namespace"
            );
        }
    }

    private synchronized int allocateRuntimeId() {
        while (this.typeIds.containsKey(this.nextRuntimeId) || Registries.ITEM_RUNTIMEID.getIdentifier(this.nextRuntimeId) != null
        ) {
            this.nextRuntimeId++;
        }

        if (this.nextRuntimeId > Short.MAX_VALUE) {
            throw new IllegalStateException(
                "No runtime item ID available"
            );
        }

        return this.nextRuntimeId++;
    }
}
