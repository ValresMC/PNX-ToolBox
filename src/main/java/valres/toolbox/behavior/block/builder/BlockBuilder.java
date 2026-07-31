package valres.toolbox.behavior.block.builder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.customblock.CustomBlock;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.component.BlockComponent;
import valres.toolbox.behavior.block.permutation.BlockPermutation;
import valres.toolbox.behavior.block.trait.BlockTrait;

public final class BlockBuilder {
	private final Block block;
	private final Map<String, BlockComponent> components = new LinkedHashMap<>();
	private final List<BlockPermutation> permutations = new java.util.ArrayList<>();
	private final Map<String, BlockTrait> traits = new LinkedHashMap<>();
	private final Set<String> tags = new LinkedHashSet<>();

	private CreativeCategory creativeCategory = CreativeCategory.CONSTRUCTION;
	private String creativeGroup = "";
	private boolean hiddenInCommands;
	private TickSettings tickSettings;
	private boolean stepSensor;

	private BlockBuilder(@NonNull Block block) {
		this.block = block;
	}

	public static @NonNull BlockBuilder create(@NonNull Block block) {
		return new BlockBuilder(block);
	}

	public @NonNull Block getBlock() {
		return this.block;
	}

	public @NonNull String getIdentifier() {
		return this.block.getId();
	}

	public @NonNull String getName() {
		String identifier = this.getIdentifier();
		int separator = identifier.indexOf(':');
		return separator < 0 ? identifier : identifier.substring(separator + 1);
	}

	public @NonNull BlockProperties getProperties() {
		return this.block.getProperties();
	}

	public @NonNull Map<String, BlockComponent> getComponents() {
		return Collections.unmodifiableMap(this.components);
	}

	public BlockComponent getComponent(@NonNull String identifier) {
		return this.components.get(identifier);
	}

	public boolean hasComponent(@NonNull String identifier) {
		return this.components.containsKey(identifier);
	}

	public void addComponent(@NonNull BlockComponent component) {
		String identifier = component.getIdentifier();
		if (identifier.isBlank()) {
			throw new IllegalArgumentException("Component identifier cannot be empty");
		}

		this.components.put(identifier, component);
	}

	public @NonNull BlockBuilder removeComponent(@NonNull String identifier) {
		this.components.remove(identifier);
		return this;
	}

	public @NonNull List<BlockPermutation> getPermutations() {
		return Collections.unmodifiableList(this.permutations);
	}

	public void addPermutation(@NonNull BlockPermutation permutation) {
		this.permutations.add(permutation);
	}

	public @NonNull Map<String, BlockTrait> getTraits() {
		return Collections.unmodifiableMap(this.traits);
	}

	public @Nullable BlockTrait getTrait(@NonNull String identifier) {
		return this.traits.get(Objects.requireNonNull(identifier, "Trait identifier cannot be null"));
	}

	public boolean hasTrait(@NonNull String identifier) {
		return this.traits.containsKey(Objects.requireNonNull(identifier, "Trait identifier cannot be null"));
	}

	public void addTrait(@NonNull BlockTrait trait) {
		Objects.requireNonNull(trait, "Block trait cannot be null");
		String identifier = Objects.requireNonNull(trait.getIdentifier(), "Trait identifier cannot be null");
		if (identifier.isBlank()) {
			throw new IllegalArgumentException("Trait identifier cannot be empty");
		}

		this.traits.put(identifier, trait);
	}

	public @NonNull BlockBuilder removeTrait(@NonNull String identifier) {
		this.traits.remove(Objects.requireNonNull(identifier, "Trait identifier cannot be null"));
		return this;
	}

	public @NonNull Set<String> getTags() {
		return Collections.unmodifiableSet(this.tags);
	}

	public void addTag(@NonNull String tag) {
		if (tag.isBlank()) {
			throw new IllegalArgumentException("Block tag cannot be empty");
		}

		this.tags.add(tag);
	}

	public @NonNull BlockBuilder setCreativePlacement(@NonNull CreativeCategory category, @NonNull String group) {
		this.creativeCategory = category;
		this.creativeGroup = group;
		return this;
	}

	public void setHiddenInCommands(boolean hidden) {
		this.hiddenInCommands = hidden;
	}

	public @NonNull BlockBuilder setTickSettings(int minimumTicks, int maximumTicks, boolean looping) {
		if (minimumTicks < 0 || maximumTicks < minimumTicks) {
			throw new IllegalArgumentException("Tick range must satisfy 0 <= minimumTicks <= maximumTicks");
		}

		this.tickSettings = new TickSettings(minimumTicks, maximumTicks, looping);
		return this;
	}

	public @NonNull BlockBuilder setStepSensor(boolean enabled) {
		this.stepSensor = enabled;
		return this;
	}

	public @NonNull CustomBlockDefinition toDefinition() {
		if (!(this.block instanceof CustomBlock customBlock)) {
			throw new IllegalStateException("Block must implement CustomBlock or ToolBoxBlock");
		}
		this.validateTraitProperties();

		CustomBlockDefinition.Builder nativeBuilder = CustomBlockDefinition.builder(customBlock);
		if (this.tickSettings != null) {
			nativeBuilder.blockTick(this.tickSettings.minimumTicks(), this.tickSettings.maximumTicks(), this.tickSettings.looping());
		}
		nativeBuilder.isStepSensor(this.stepSensor);

		return nativeBuilder.customBuild(this::writeDefinition);
	}

	private void writeDefinition(@NonNull CompoundTag root) {
		CompoundTag componentData = new CompoundTag();
		for (Map.Entry<String, BlockComponent> entry : this.components.entrySet()) {
			componentData.put(entry.getKey(), entry.getValue().toNBT());
		}
		root.putCompound("components", componentData);

		if (this.permutations.isEmpty()) {
			root.remove("permutations");
		} else {
			ListTag<CompoundTag> permutationData = new ListTag<>(Tag.TAG_Compound);
			for (BlockPermutation permutation : this.permutations) {
				permutationData.add(permutation.toNBT());
			}
			root.putList("permutations", permutationData);
		}

		ListTag<CompoundTag> traitData = new ListTag<>(Tag.TAG_Compound);
		for (BlockTrait trait : this.traits.values()) {
			traitData.add(trait.toNBT());
		}
		root.putList("traits", traitData);

		ListTag<StringTag> blockTags = new ListTag<>(Tag.TAG_String);
		for (String tag : this.tags) {
			blockTags.add(new StringTag(tag));
		}
		root.putList("blockTags", blockTags);

		root.putCompound("menu_category", new CompoundTag().putString("category", this.creativeCategory.name().toLowerCase(java.util.Locale.ROOT)).putString("group", this.creativeGroup).putByte("is_hidden_in_commands", (byte) (this.hiddenInCommands ? 1 : 0)));
		root.putInt("molangVersion", 13);
	}

	private void validateTraitProperties() {
		Map<String, BlockPropertyType<?>> declaredProperties = new LinkedHashMap<>();
		for (BlockPropertyType<?> property : this.getProperties().getPropertyTypeSet()) {
			declaredProperties.put(property.getName(), property);
		}

		for (BlockTrait trait : this.traits.values()) {
			for (BlockPropertyType<?> required : trait.getProvidedProperties()) {
				BlockPropertyType<?> declared = declaredProperties.get(required.getName());
				if (declared == null) {
					throw new IllegalStateException("Block '" + this.getIdentifier() + "' must declare the trait property '" + required.getName() + "' in its BlockProperties");
				}
				if (declared.getType() != required.getType() || !normalizedValues(declared).equals(normalizedValues(required))) {
					throw new IllegalStateException("Block property '" + required.getName() + "' does not match the values required by trait '" + trait.getIdentifier() + "'");
				}
			}
		}
	}

	private static @NonNull List<Object> normalizedValues(@NonNull BlockPropertyType<?> property) {
		return Objects.requireNonNull(property, "Block property cannot be null").getValidValues().stream().map(value -> {
			if (value instanceof Enum<?> enumValue) {
				return enumValue.name().toLowerCase(java.util.Locale.ROOT);
			}
			return value;
		}).toList();
	}

	private record TickSettings(int minimumTicks, int maximumTicks, boolean looping) {
	}
}
