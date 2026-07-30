package valres.toolbox.behavior.item;

import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.powernukkitx.item.Item;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.RegisterException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final public class PNXItemRegistryAccessor {
    final private static Field constructorsField;

    final private static Map<ClassLoader, FastMemberLoader> loaders = new ConcurrentHashMap<>();

    static {
        try {
            constructorsField = ItemRegistry.class.getDeclaredField("CACHE_CONSTRUCTORS");
            constructorsField.setAccessible(true);
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private PNXItemRegistryAccessor() {
    }

    @SuppressWarnings("unchecked")
    public static void register(
        String identifier,
        Class<? extends Item> itemClass
    ) throws RegisterException {
        try {
            FastMemberLoader loader = loaders.computeIfAbsent(itemClass.getClassLoader(), FastMemberLoader::new);

            FastConstructor<? extends Item> constructor = FastConstructor.create(
                itemClass.getConstructor(),
                loader,
                false
            );

            Map<String, FastConstructor<? extends Item>> constructors = (Map<String, FastConstructor<? extends Item>>) constructorsField.get(null);

            if (constructors.putIfAbsent(identifier, constructor) != null) {
                throw new RegisterException(
                    "Item '" + identifier + "' is already registered"
                );
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
