package valres.toolbox.behavior.item;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.ItemRuntimeIdRegistry;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;
import valres.toolbox.behavior.creative.CreativeInventoryManager;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.builder.ItemBuilder;
import valres.toolbox.behavior.item.builder.LegacyItemBuilder;

public final class CustomItemRegistry {
	private static final CustomItemRegistry INSTANCE = new CustomItemRegistry();

	private final Map<String, RegisteredItemData> items = new HashMap<>();
	private final Map<Integer, String> typeIds = new HashMap<>();

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
		Item item = this.createItem(itemClass);

		String identifier = item.getId();
		this.valideIdentifier(identifier);

		switch (ItemVersionResolver.fromItem(item)) {
			case LEGACY -> this.registerLegacyItem(identifier, item);
			case DATA_DRIVEN -> this.registerDataDrivenItem(identifier, item);
			case NONE -> throw new IllegalStateException("Unsupported item format");
		}
	}

	/**
	 * Registers the custom item form of a custom block with its own item runtime
	 * ID. The block registry must suppress its default ItemBlock.
	 */
	public void registerBlockItem(Class<? extends Item> itemClass, String blockIdentifier) {
		Item item = this.createItem(itemClass);
		String identifier = item.getId();

		this.valideIdentifier(identifier);
		if (!identifier.equals(blockIdentifier)) {
			throw new IllegalArgumentException("Block item identifier '" + identifier + "' must match block identifier '" + blockIdentifier + "'");
		}

		Block result = item.getBlock();
		if (result.isAir() || !result.getId().equals(blockIdentifier)) {
			throw new IllegalArgumentException("Block item '" + identifier + "' must return its linked block from getBlock()");
		}

		ItemVersion format = ItemVersionResolver.fromItem(item);
		if (format != ItemVersion.LEGACY) {
			throw new IllegalStateException("A linked block item must be a Legacy item");
		}

		this.registerLegacyItem(identifier, item, this.allocateRuntimeId());
	}

	public void registerLegacyItem(String identifier, Item item) {
		this.valideIdentifier(identifier);

		ItemVersion format = ItemVersionResolver.fromItem(item);
		if (format != ItemVersion.LEGACY) {
			throw new IllegalStateException("Item must be a Legacy item");
		}

		this.registerLegacyItem(identifier, item, this.allocateRuntimeId());
	}

	private void registerLegacyItem(String identifier, Item item, int runtimeId) {
		LegacyItemBuilder builder = LegacyItemBuilder.create(item).setRuntimeId(runtimeId);

		this.applyItemComponents(builder);
		this.deepRegister(builder);
	}

	public void registerDataDrivenItem(String identifier, Item item) {
		this.valideIdentifier(identifier);

		ItemVersion format = ItemVersionResolver.fromItem(item);
		if (format != ItemVersion.DATA_DRIVEN) {
			throw new IllegalStateException("Item must be a Data-Driven item");
		}

		int runtimeId = CustomItemDefinition.ensureRuntimeIdAllocated(identifier);
		DataDrivenItemBuilder builder = DataDrivenItemBuilder.create(item).setRuntimeId(runtimeId);

		this.applyItemComponents(builder);
		this.deepRegister(builder);
	}

	public void applyItemComponents(ItemBuilder<?> builder) {
		Item item = builder.getItem();

		ItemDataResolver.applyDefault(builder);

		if (builder instanceof DataDrivenItemBuilder builder_) {
			if (item instanceof LegacyExtraComponentsInterface) {
				throw new IllegalStateException("Data-Driven item cannot define legacy components");
			}

			if (item instanceof DataDrivenExtraComponentsInterface item_) {
				(item_).defineDataDrivenComponent(builder_);
			}

			CreativeInventoryManager.getInstance().applyDefinition(builder_);
		} else if (builder instanceof LegacyItemBuilder builder_) {
			if (item instanceof DataDrivenExtraComponentsInterface) {
				throw new IllegalStateException("Legacy item cannot define data-driven components");
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
			throw new IllegalStateException("Item '" + identifier + "' is already registered");
		}

		if (this.typeIds.containsKey(runtimeId)) {
			throw new IllegalStateException("Runtime ID '" + runtimeId + "' is already registered");
		}

		String registeredIdentifier = Registries.ITEM_RUNTIMEID.getIdentifier(runtimeId);
		if (registeredIdentifier != null) {
			throw new IllegalStateException("Runtime ID '" + runtimeId + "' is already registered by '" + registeredIdentifier + "'");
		}

		CompoundTag definitionNbt = builder.toNBT();
		NbtMap componentData = definitionNbt.toNetwork();

		try {
			PNXItemRegistryAccessor.register(identifier, item.getClass().asSubclass(Item.class));
			if (builder.getFormat() == ItemVersion.DATA_DRIVEN) {
				PNXItemRegistryAccessor.registerCustomDefinition(new CustomItemDefinition(identifier, definitionNbt));
			}

			Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(new ItemRuntimeIdRegistry.RuntimeEntry(identifier, runtimeId, builder.getFormat() == ItemVersion.DATA_DRIVEN));
		} catch (RegisterException exception) {
			throw new IllegalStateException("Cannot register item '" + identifier + "'", exception);
		}

		RegisteredItemData data = new RegisteredItemData(item, item.getClass(), runtimeId, builder.getFormat(), componentData);

		this.items.put(identifier, data);
		this.typeIds.put(runtimeId, identifier);

		CreativeInventoryManager.getInstance().addToCreative(item);
	}

	private void valideIdentifier(String identifier) {
		if (identifier.trim().isEmpty()) {
			throw new IllegalArgumentException("Identifier cannot be empty");
		}

		if (!identifier.contains(":")) {
			throw new IllegalArgumentException("Identifier must be namespaced");
		}

		if (identifier.startsWith("minecraft:")) {
			throw new IllegalArgumentException("Identifier cannot use the minecraft namespace");
		}
	}

	private Item createItem(Class<? extends Item> itemClass) {
		try {
			return itemClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException exception) {
			throw new IllegalArgumentException("Item class must have a public no-argument constructor", exception);
		}
	}

	private synchronized int allocateRuntimeId() {
		while (this.typeIds.containsKey(this.nextRuntimeId) || Registries.ITEM_RUNTIMEID.getIdentifier(this.nextRuntimeId) != null) {
			this.nextRuntimeId++;
		}

		if (this.nextRuntimeId > Short.MAX_VALUE) {
			throw new IllegalStateException("No runtime item ID available");
		}

		return this.nextRuntimeId++;
	}
}
