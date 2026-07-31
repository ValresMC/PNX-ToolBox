package valres.toolbox.behavior.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.registry.BlockRegistry;
import org.powernukkitx.registry.ItemRuntimeIdRegistry;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;
import valres.toolbox.ToolBox;

public final class PNXBlockRegistryAccessor {
	private static final Field keysField;
	private static final Field constructorsField;
	private static final Field stateConstructorsField;
	private static final Field propertiesField;
	private static final Field definitionsField;
	private static final Field definitionsByIdField;

	private static final Map<ClassLoader, FastMemberLoader> loaders = new ConcurrentHashMap<>();

	static {
		try {
			keysField = accessibleField("KEYSET");
			constructorsField = accessibleField("CACHE_CONSTRUCTORS");
			stateConstructorsField = accessibleField("CACHE_CONSTRUCTORS_BY_HASH");
			propertiesField = accessibleField("PROPERTIES");
			definitionsField = accessibleField("CUSTOM_BLOCK_DEFINITIONS");
			definitionsByIdField = accessibleField("CUSTOM_BLOCK_DEFINITION_BY_ID");
		} catch (ReflectiveOperationException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private PNXBlockRegistryAccessor() {
	}

	@SuppressWarnings("unchecked") public static void register(Block block, Class<? extends Block> blockClass, CustomBlockDefinition definition) throws RegisterException {
		register(block, blockClass, definition, true);
	}

	@SuppressWarnings("unchecked") public static void register(Block block, Class<? extends Block> blockClass, CustomBlockDefinition definition, boolean registerDefaultItem) throws RegisterException {
		String identifier = block.getId();
		BlockProperties properties = block.getProperties();

		try {
			Map<String, FastConstructor<? extends Block>> constructors = (Map<String, FastConstructor<? extends Block>>) constructorsField.get(null);
			if (constructors.containsKey(identifier)) {
				throw new RegisterException("Block '" + identifier + "' is already registered");
			}

			FastMemberLoader loader = loaders.computeIfAbsent(blockClass.getClassLoader(), FastMemberLoader::new);
			FastConstructor<? extends Block> constructor = FastConstructor.create(blockClass.getConstructor(BlockState.class), loader, false);
			constructors.put(identifier, constructor);

			if (registerDefaultItem) {
				Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(new ItemRuntimeIdRegistry.RuntimeEntry(identifier, resolveItemRuntimeId(definition), false));
			}

			Map<Integer, FastConstructor<? extends Block>> stateConstructors = (Map<Integer, FastConstructor<? extends Block>>) stateConstructorsField.get(null);
			for (BlockState state : properties.getSpecialValueMap().values()) {
				Registries.BLOCKSTATE.registerInternal(state);
				stateConstructors.putIfAbsent(state.blockStateHash(), constructor);
			}

			((Set<String>) keysField.get(null)).add(identifier);
			((Map<String, BlockProperties>) propertiesField.get(null)).put(identifier, properties);

			Plugin owner = ToolBox.getInstance();
			Map<Plugin, List<CustomBlockDefinition>> definitions = (Map<Plugin, List<CustomBlockDefinition>>) definitionsField.get(null);
			definitions.computeIfAbsent(owner, ignored -> new ArrayList<>()).add(definition);
			((Map<String, CustomBlockDefinition>) definitionsByIdField.get(null)).put(identifier, definition);

			if (registerDefaultItem && Registries.CREATIVE.shouldBeRegisteredBlock(definition.nbt())) {
				ItemBlock item = new ItemBlock(block);
				item.setNetId(null);
				int groupIndex = Registries.CREATIVE.resolveGroupIndexFromBlockDefinition(identifier, definition.nbt());
				Registries.CREATIVE.addCreativeItem(item, groupIndex);
			}
		} catch (RegisterException exception) {
			throw exception;
		} catch (ReflectiveOperationException exception) {
			throw new RegisterException(exception);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static int resolveItemRuntimeId(CustomBlockDefinition definition) {
		return 255 - definition.getRuntimeId();
	}

	private static Field accessibleField(String name) throws NoSuchFieldException {
		Field field = BlockRegistry.class.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}
}
