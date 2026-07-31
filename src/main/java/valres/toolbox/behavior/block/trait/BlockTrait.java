package valres.toolbox.behavior.block.trait;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.component.ComponentNbtHelper;

public abstract class BlockTrait {
	protected abstract @NonNull List<?> enabledStates();

	protected @NonNull Map<String, ?> values() {
		return Map.of();
	}

	public abstract @NonNull String getIdentifier();

	public @NonNull List<BlockPropertyType<?>> getProvidedProperties() {
		return List.of();
	}

	public final BlockPropertyType<?> @NonNull [] getProvidedPropertiesArray() {
		return this.getProvidedProperties().toArray(BlockPropertyType<?>[]::new);
	}

	public @NonNull CompoundTag toNBT() {
		String identifier = Objects.requireNonNull(this.getIdentifier(), "Trait identifier cannot be null");
		if (identifier.isBlank()) {
			throw new IllegalStateException("Trait identifier cannot be empty");
		}

		CompoundTag enabledStates = new CompoundTag();
		for (Object state : Objects.requireNonNull(this.enabledStates(), "Enabled trait states cannot be null")) {
			enabledStates.putByte(stateName(state), (byte) 1);
		}

		CompoundTag tag = new CompoundTag().putString("name", identifier).putCompound("enabled_states", enabledStates);

		for (Map.Entry<String, ?> entry : Objects.requireNonNull(this.values(), "Trait values cannot be null").entrySet()) {
			String name = Objects.requireNonNull(entry.getKey(), "Trait value name cannot be null");
			Object value = entry.getValue();
			if (value != null) {
				tag.put(name, value instanceof Tag existing ? existing : ComponentNbtHelper.tag(value));
			}
		}

		return tag;
	}

	protected static @NonNull String stateName(@NonNull Object state) {
		String name = Objects.requireNonNull(state, "Trait state cannot be null").toString();
		return name.startsWith("minecraft:") ? name.substring(10) : name;
	}

	protected static @NonNull String fullStateName(@NonNull Object state) {
		String name = Objects.requireNonNull(state, "Trait state cannot be null").toString();
		return name.contains(":") ? name : "minecraft:" + name;
	}
}
