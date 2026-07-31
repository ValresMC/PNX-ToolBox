package valres.toolbox.behavior.item.builder;

import java.util.Objects;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;

public abstract class ItemBuilder<T extends ItemBuilder<T>> {
	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";

	public static final String TAG_COMPONENTS = "components";
	public static final String TAG_ITEM_PROPERTIES = "item_properties";

	private final Item item;
	private final String identifier;

	private Integer runtimeId;

	protected ItemBuilder(@NonNull Item item) {
		this.item = Objects.requireNonNull(item, "Item cannot be null");
		this.identifier = item.getId();
		if (this.identifier == null || this.identifier.isBlank()) {
			throw new IllegalArgumentException("Item identifier cannot be empty");
		}
	}

	public abstract ItemVersion getFormat();

	public abstract CompoundTag toNBT();

	protected abstract T self();

	public @NonNull Item getItem() {
		return this.item;
	}

	public @NonNull String getIdentifier() {
		return this.identifier;
	}

	public int getRuntimeId() {
		if (this.runtimeId == null) {
			throw new IllegalStateException("Runtime ID has not been assigned to item " + this.identifier);
		}

		return this.runtimeId;
	}

	public boolean hasRuntimeId() {
		return this.runtimeId != null;
	}

	public T setRuntimeId(int runtimeId) {
		if (runtimeId < 0) {
			throw new IllegalArgumentException("Item runtime ID cannot be negative");
		}

		this.runtimeId = runtimeId;
		return this.self();
	}
}
