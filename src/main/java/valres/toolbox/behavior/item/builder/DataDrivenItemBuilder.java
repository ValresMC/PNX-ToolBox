package valres.toolbox.behavior.item.builder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.tags.ItemTags;
import valres.toolbox.behavior.item.components.DataDrivenItemComponent;
import valres.toolbox.behavior.item.components.TagsComponent;
import valres.toolbox.behavior.item.properties.DataDrivenItemProperty;

public class DataDrivenItemBuilder extends ItemBuilder<DataDrivenItemBuilder> {
	private final Map<String, DataDrivenItemComponent> components = new LinkedHashMap<>();
	private final Map<String, DataDrivenItemProperty> properties = new LinkedHashMap<>();

	protected DataDrivenItemBuilder(Item item) {
		super(item);
	}

	public static DataDrivenItemBuilder create(Item item) {
		return new DataDrivenItemBuilder(item);
	}

	@Override public ItemVersion getFormat() {
		return ItemVersion.DATA_DRIVEN;
	}

	@Override public CompoundTag toNBT() {
		CompoundTag components = new CompoundTag();
		CompoundTag properties = new CompoundTag();

		for (Map.Entry<String, DataDrivenItemProperty> entry : this.properties.entrySet()) {
			String identifier = entry.getKey();
			DataDrivenItemProperty property = entry.getValue();

			properties.put(identifier, property.toNBT());
		}

		for (Map.Entry<String, DataDrivenItemComponent> entry : this.components.entrySet()) {
			String identifier = entry.getKey();
			DataDrivenItemComponent component = entry.getValue();

			components.put(identifier, component.toNBT());
			this.registerTags(component);
		}

		components.putCompound(ItemBuilder.TAG_ITEM_PROPERTIES, properties);

		return new CompoundTag().putInt(ItemBuilder.TAG_ID, this.getRuntimeId()).putString(ItemBuilder.TAG_NAME, this.getIdentifier()).putCompound(ItemBuilder.TAG_COMPONENTS, components);
	}

	public Map<String, DataDrivenItemComponent> getComponents() {
		return Collections.unmodifiableMap(this.components);
	}

	public void addComponent(@NonNull DataDrivenItemComponent component) {
		String identifier = component.getIdentifier();
		if (identifier.isBlank()) {
			throw new IllegalArgumentException("Component identifier cannot be empty");
		}

		this.components.put(identifier, component);
	}

	public DataDrivenItemBuilder removeComponent(@NonNull String componentId) {
		this.components.remove(componentId);
		return this;
	}

	public boolean hasComponent(String componentId) {
		return this.components.containsKey(componentId);
	}

	public Map<String, DataDrivenItemProperty> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	public void addProperty(@NonNull DataDrivenItemProperty property) {
		String identifier = property.getIdentifier();
		if (identifier.isBlank()) {
			throw new IllegalArgumentException("Property identifier cannot be empty");
		}

		this.properties.put(identifier, property);
	}

	public DataDrivenItemBuilder removeProperty(@NonNull String propertyId) {
		this.properties.remove(propertyId);
		return this;
	}

	public boolean hasProperty(String propertyId) {
		return this.properties.containsKey(propertyId);
	}

	@Override protected DataDrivenItemBuilder self() {
		return this;
	}

	private void registerTags(DataDrivenItemComponent component) {
		if (!(component instanceof TagsComponent tagsComponent)) {
			return;
		}

		ItemTags.register(this.getIdentifier(), tagsComponent.getTags());
	}
}
