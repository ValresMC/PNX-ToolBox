package valres.toolbox.behavior.block;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.customblock.CustomBlock;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.registry.RegisterException;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.item.CustomItemRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final public class CustomBlockRegistry {
    final private static CustomBlockRegistry INSTANCE = new CustomBlockRegistry();

    final private Map<String, RegisteredBlockData> blocks = new LinkedHashMap<>();

    private CustomBlockRegistry() {
    }

    public static @NonNull CustomBlockRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized @NonNull BlockBuilder register(
        @NonNull Class<? extends Block> blockClass
    ) {
        Block block = this.createBlock(blockClass);
        return this.register(BlockBuilder.create(block));
    }

    public synchronized @NonNull BlockBuilder register(
        @NonNull Class<? extends Block> blockClass,
        @NonNull Class<? extends Item> blockItemClass
    ) {
        Block block = this.createBlock(blockClass);
        return this.register(BlockBuilder.create(block), blockItemClass);
    }

    public synchronized @NonNull BlockBuilder register(
        @NonNull BlockBuilder builder
    ) {
        return this.registerInternal(builder, null);
    }

    public synchronized @NonNull BlockBuilder register(
        @NonNull BlockBuilder builder,
        @NonNull Class<? extends Item> blockItemClass
    ) {
        return this.registerInternal(builder, blockItemClass);
    }

    private @NonNull BlockBuilder registerInternal(
        @NonNull BlockBuilder builder,
        @Nullable Class<? extends Item> blockItemClass
    ) {
        Block block = builder.getBlock();
        String identifier = builder.getIdentifier();

        this.validateBlockClass(block);
        this.validateIdentifier(identifier);
        if (!(block instanceof CustomBlock)) {
            throw new IllegalArgumentException(
                "Block class must implement CustomBlock or ToolBoxBlock"
            );
        }
        if (this.blocks.containsKey(identifier)) {
            throw new IllegalStateException(
                "Block '" + identifier + "' is already registered"
            );
        }

        BlockDataResolver.applyDefault(builder);
        CustomBlockDefinition definition = builder.toDefinition();
        boolean hasLinkedItem = blockItemClass != null;

        if (hasLinkedItem) {
            CustomItemRegistry.getInstance().registerBlockItem(
                blockItemClass,
                identifier
            );
        }

        try {
            PNXBlockRegistryAccessor.register(
                block,
                block.getClass().asSubclass(Block.class),
                definition,
                !hasLinkedItem
            );
        } catch (RegisterException exception) {
            throw new IllegalStateException(
                "Cannot register block '" + identifier + "'", exception
            );
        }

        if (BlockDataResolver.isCrop(block)) {
            Level.setCanRandomTick(identifier, true);
        }

        this.blocks.put(
            identifier,
            new RegisteredBlockData(
                block,
                block.getClass().asSubclass(Block.class),
                builder,
                definition
            )
        );

        return builder;
    }

    public @Nullable RegisteredBlockData get(@NonNull String identifier) {
        return this.blocks.get(identifier);
    }

    public @NonNull Map<String, RegisteredBlockData> getAll() {
        return Collections.unmodifiableMap(this.blocks);
    }

    @NonNull CustomBlockDefinition resolveDefinition(@NonNull Block block) {
        RegisteredBlockData registered = this.blocks.get(block.getId());
        if (registered != null) {
            return registered.definition();
        }

        BlockBuilder builder = BlockBuilder.create(block);
        BlockDataResolver.applyDefault(builder);
        return builder.toDefinition();
    }

    private @NonNull Block createBlock(
        @NonNull Class<? extends Block> blockClass
    ) {
        try {
            Constructor<? extends Block> constructor = blockClass.getConstructor(
                BlockState.class
            );
            return constructor.newInstance((BlockState) null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                "Block class must have a public constructor accepting BlockState",
                exception
            );
        }
    }

    private void validateBlockClass(@NonNull Block block) {
        Class<? extends Block> blockClass = block.getClass().asSubclass(
            Block.class
        );
        if (Modifier.isAbstract(blockClass.getModifiers())) {
            throw new IllegalArgumentException(
                "Block class cannot be abstract"
            );
        }

        try {
            Field propertiesField = blockClass.getDeclaredField("PROPERTIES");
            int modifiers = propertiesField.getModifiers();
            boolean validField = Modifier.isPublic(modifiers)
                && Modifier.isStatic(modifiers)
                && Modifier.isFinal(modifiers)
                && propertiesField.getType() == BlockProperties.class;
            if (!validField) {
                throw new IllegalArgumentException(
                    "Block class must declare public static final "
                        + "BlockProperties PROPERTIES"
                );
            }
            if (propertiesField.get(null) != block.getProperties()) {
                throw new IllegalArgumentException(
                    "getProperties() must return the class PROPERTIES field"
                );
            }
            if (blockClass.getMethod("getProperties").getDeclaringClass()
                != blockClass) {
                throw new IllegalArgumentException(
                    "Block class must override getProperties()"
                );
            }
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException(
                "Block class must declare public static final "
                    + "BlockProperties PROPERTIES",
                exception
            );
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                "Cannot inspect block properties",
                exception
            );
        }
    }

    private void validateIdentifier(@NonNull String identifier) {
        if (identifier.isBlank()) {
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
}
