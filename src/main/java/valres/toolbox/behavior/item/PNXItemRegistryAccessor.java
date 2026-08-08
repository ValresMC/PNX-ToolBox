package valres.toolbox.behavior.item;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.RegisterException;

public final class PNXItemRegistryAccessor {
	private static final Field constructorsField;
	private static final Field customDefinitionsField;

	private static final Map<ClassLoader, FastMemberLoader> loaders = new ConcurrentHashMap<>();

	static {
		try {
			constructorsField = ItemRegistry.class.getDeclaredField("CACHE_CONSTRUCTORS");
			constructorsField.setAccessible(true);
			customDefinitionsField = ItemRegistry.class.getDeclaredField("CUSTOM_ITEM_DEFINITIONS");
			customDefinitionsField.setAccessible(true);
		} catch (ReflectiveOperationException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	@SuppressWarnings("unchecked") public static void registerCustomDefinition(CustomItemDefinition definition) throws RegisterException {
		try {
			Map<String, CustomItemDefinition> definitions = (Map<String, CustomItemDefinition>) customDefinitionsField.get(null);
			if (definitions.putIfAbsent(definition.identifier(), definition) != null) {
				throw new RegisterException("Custom item definition '" + definition.identifier() + "' is already registered");
			}
		} catch (RegisterException exception) {
			throw exception;
		} catch (ReflectiveOperationException exception) {
			throw new RegisterException(exception);
		}
	}

	private PNXItemRegistryAccessor() {
	}

	@SuppressWarnings("unchecked") public static void register(String identifier, Class<? extends Item> itemClass) throws RegisterException {
		try {
			FastMemberLoader loader = loaders.computeIfAbsent(itemClass.getClassLoader(), FastMemberLoader::new);

			FastConstructor<? extends Item> constructor = FastConstructor.create(itemClass.getConstructor(), loader, false);

			Map<String, FastConstructor<? extends Item>> constructors = (Map<String, FastConstructor<? extends Item>>) constructorsField.get(null);

			if (constructors.putIfAbsent(identifier, constructor) != null) {
				throw new RegisterException("Item '" + identifier + "' is already registered");
			}
		} catch (RegisterException exception) {
			throw exception;
		} catch (ReflectiveOperationException exception) {
			throw new RegisterException(exception);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
