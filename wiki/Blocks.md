# Blocks

PNX-ToolBox registers custom PowerNukkitX blocks, builds their Bedrock
components from server-side behavior, and keeps their states, permutations,
and traits synchronized.

This guide uses the identifier `example:ruby_block`. Always replace `example`
with your plugin's namespace.

## Before: native PNX block

With PowerNukkitX alone, the block must implement `CustomBlock`, provide its
client definition, and repeat several values that already exist in the
server-side class:

```java
public final class RubyBlock extends Block implements CustomBlock {
    public static final String IDENTIFIER = "example:ruby_block";

    public static final BlockProperties PROPERTIES = new BlockProperties(
        IDENTIFIER
    );

    public RubyBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        return CustomBlockDefinition.builder(this)
            .name("Ruby Block")
            .texture("ruby_block")
            .geometry("minecraft:geometry.full_block")
            .destructibleByMining(16.6667f)
            .destructibleByExplosion(6)
            .friction((float) getFrictionFactor())
            .lightDampening(getLightFilter())
            .lightEmission(getLightLevel())
            .mapColor("#FFFFFF")
            .creativeCategory(CreativeCategory.CONSTRUCTION)
            .build();
    }
}
```

Native registration also requires the owning plugin and explicit
`RegisterException` handling:

```java
@Override
public void onLoad() {
    try {
        Registries.BLOCK.registerCustomBlock(this, RubyBlock.class);
    } catch (RegisterException exception) {
        throw new IllegalStateException(
            "Unable to register RubyBlock",
            exception
        );
    }
}
```

## After: the same block with ToolBox

`ToolBoxBlock` supplies `getDefinition()` automatically. The class only
describes behavior that is actually used by the server:

```java
public final class RubyBlock extends Block implements ToolBoxBlock {
    public static final String IDENTIFIER = "example:ruby_block";

    public static final BlockProperties PROPERTIES = new BlockProperties(
        IDENTIFIER
    );

    public RubyBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6;
    }
}
```

```java
@Override
public void onLoad() {
    CustomBlockRegistry.getInstance().register(RubyBlock.class);
}
```

ToolBox then generates the following data automatically:

| Data | Source |
| --- | --- |
| Geometry | `minecraft:geometry.full_block` |
| Texture | Identifier path after `:` |
| Render method | Transparency reported by the block |
| Display name | `tile.<identifier>.name` |
| Collision and selection | The block's PNX boxes |
| Mining time | `getHardness()` |
| Explosion resistance | `getResistance()` |
| Friction | `getFrictionFactor()` |
| Lighting | `getLightFilter()` and `getLightLevel()` |
| Map color | `getColor()` |
| Tags | `getTags()` |
| Creative inventory | Automatic detection or annotation |
| Crop permutations | Block type and growth property |

## Block class requirements

A class registered through `CustomBlockRegistry` must meet these requirements:

- It must be public and non-abstract.
- It must extend `Block` or one of its subclasses.
- It should implement `ToolBoxBlock` to use the generated definition.
- It must declare `public static final BlockProperties PROPERTIES`.
- It must have a public constructor accepting `BlockState`.
- It must override `getProperties()` and return exactly `PROPERTIES`.
- Its identifier must be namespaced and must not use `minecraft:`.
- It must be registered only once, preferably during `onLoad()`.

It is technically possible to implement `CustomBlock` directly, but then you
must maintain `getDefinition()` yourself. `ToolBoxBlock` is therefore the
recommended form when using the ToolBox registry.

## Hardness and resistance

These two values control different behavior:

```java
@Override
public double getHardness() {
    return 5;
}

@Override
public double getResistance() {
    return 6;
}
```

- `getHardness()` controls mining difficulty and mining time.
- `getResistance()` controls explosion resistance.
- `double` is Java's 64-bit decimal number type; `5`, `5.0`, and `5.5` are all
  valid return values.
- A negative value becomes a non-destructible component for the corresponding
  destruction type.

## Creative inventory

Without an annotation, a block is placed in the Construction category. Use
`@CreativeInventoryInfo` to select another category or group:

```java
@CreativeInventoryInfo(
    category = CreativeCategory.NATURE,
    group = CreativeGroup.ORE
)
public final class RubyOreBlock extends Block implements ToolBoxBlock {
    // PROPERTIES, constructor, and block methods...
}
```

Hide a block from the creative inventory and commands with:

```java
@CreativeInventoryInfo(hidden = true)
```

Use `customGroup = "My Group"` for a custom creative group that has already
been registered through PNX. Do not set both `group` and `customGroup`.

## Adding or replacing components

Implement `ExtraBlockComponentsInterface` to access the `BlockBuilder` after
automatic values have been applied:

```java
public final class RubyLamp extends Block
    implements ToolBoxBlock, ExtraBlockComponentsInterface {

    public static final BlockProperties PROPERTIES = new BlockProperties(
        "example:ruby_lamp"
    );

    public RubyLamp(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public void defineBlockComponents(BlockBuilder builder) {
        builder.addComponent(new LightEmissionComponent(15));
        builder.addComponent(MaterialInstancesComponent.sided(
            "ruby_lamp_side",
            "ruby_lamp_top",
            "ruby_lamp_bottom",
            RenderMethod.OPAQUE
        ));
    }
}
```

Components are indexed by their Bedrock identifier. The last component added
for an identifier replaces the previous value:

```java
builder.addComponent(new LightEmissionComponent(15));
builder.removeComponent(BlockComponentNames.COLLISION_BOX);
```

Use `RawBlockComponent` when a new Bedrock component does not yet have a typed
wrapper:

```java
builder.addComponent(new RawBlockComponent(
    "example:custom_component",
    new CompoundTag().putBoolean("enabled", true)
));
```

### Block component catalog

| Category | Available components |
| --- | --- |
| Display | `DisplayNameBlockComponent`, `GeometryComponent`, `MaterialInstancesComponent`, `ItemVisualComponent`, `EmbeddedVisualComponent`, `MapColorComponent`, `TransformationComponent`, `DestructionParticlesComponent`, `RandomOffsetComponent` |
| Shape and physics | `CollisionBoxComponent`, `SelectionBoxComponent`, `FrictionBlockComponent`, `EntityFallOnComponent`, `SupportComponent` |
| Destruction and lighting | `DestructibleByMiningComponent`, `DestructibleByExplosionComponent`, `LightDampeningComponent`, `LightEmissionComponent` |
| Placement and neighbors | `ConnectionRuleComponent`, `PlacementFilterComponent`, `ReplaceableComponent`, `FlowerPottableComponent`, `ChestObstructionComponent`, `PrecipitationInteractionsComponent` |
| Liquids and movement | `LiquidDetectionComponent`, `MovableComponent`, `LeashableComponent`, `FlammableBlockComponent` |
| Redstone | `RedstoneConductivityComponent`, `RedstoneConsumerComponent`, `RedstoneProducerComponent` |
| Features | `CraftingTableComponent`, `LootComponent`, `TickComponent`, `CustomComponentsComponent`, `OnPlayerPlacingComponent` |
| Low-level | `RawBlockComponent` |

The `BlockBox`, `BlockVisual`, `MaterialInstance`, `RenderMethod`, `BlockFace`,
`CardinalDirection`, `Range`, and `RandomOffsetAxis` types let you build nested
NBT structures without assembling them manually.

## States and permutations

Server-side states remain declared through PNX `BlockProperties`:

```java
public static final BlockProperties PROPERTIES = new BlockProperties(
    "example:ruby_lamp",
    CommonBlockProperties.POWERED_BIT
);
```

A permutation changes client-side components according to a state:

```java
@Override
public void defineBlockComponents(BlockBuilder builder) {
    builder.addPermutation(
        new BlockPermutation(BlockStateQuery.equals(
            CommonBlockProperties.POWERED_BIT.getName(),
            true
        )).addComponent(new LightEmissionComponent(15))
    );
}
```

`BlockStateQuery.equals()` and `notEquals()` escape state names and values to
produce valid Molang conditions.

### Reusable resolver

Create a resolver when several block families share the same permutation
rules:

```java
public final class PoweredLampResolver extends PermutationsResolver {
    @Override
    public boolean supports(BlockBuilder builder) {
        return builder.getBlock() instanceof RubyLamp;
    }

    @Override
    public void resolve(BlockBuilder builder) {
        builder.addPermutation(
            new BlockPermutation(BlockStateQuery.equals(
                CommonBlockProperties.POWERED_BIT.getName(),
                true
            )).addComponent(new LightEmissionComponent(15))
        );
    }
}
```

Register the resolver before the affected blocks:

```java
PermutationsResolver.register(new PoweredLampResolver());
CustomBlockRegistry.getInstance().register(RubyLamp.class);
```

Resolvers run from the lowest to the highest `getPriority()`. They can also be
discovered through `ServiceLoader` by declaring
`META-INF/services/valres.toolbox.behavior.block.permutation.PermutationsResolver`.

## Crops: before and after

With PNX alone, `getDefinition()` must manually create one permutation per
growth stage, including its Molang condition, texture, and selection box.

With ToolBox, a block extending `BlockCrops` or `BlockNetherWart` receives
these permutations automatically:

```java
public final class RubyCrop extends BlockCrops implements ToolBoxBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(
        "example:ruby_crop",
        CommonBlockProperties.GROWTH
    );

    public RubyCrop(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
```

The built-in resolver:

- Reads the block's `growth` or `age` property.
- Uses `geometry.crop`.
- Disables collision.
- Adds the `minecraft:crop` tag.
- Adjusts selection height for each stage.
- Uses `ruby_crop_0` through `ruby_crop_7` for a crop with eight stages.

## Custom furnaces

`BlockCustomFurnace` is a PNX-native furnace base. It keeps the real
`FurnaceTypeInventory`, furnace recipes, fuel and smelt events, lock handling,
hopper access and comparator output supplied by PowerNukkitX.

Bedrock closes a furnace UI tied directly to a custom block because it does not
recognize that block identifier as a vanilla furnace. The block entity therefore
uses `InventoryMenu` with `MenuType.FURNACE` as its presentation layer and binds
it to the real furnace inventory. Only the player receives the temporary vanilla
furnace block required by the client; the world block and its server inventory
are never replaced. Slot changes, progress bars, furnace experience and custom
titles remain synchronized.

Only one block class is required for each furnace type:

```java
public final class MithrilFurnace extends BlockCustomFurnace {
    public static final String IDENTIFIER = "example:mithril_furnace";

    public static final BlockProperties PROPERTIES =
        createFurnaceProperties(IDENTIFIER);

    public MithrilFurnace(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Mithril Furnace";
    }

    @Override
    public int getCookingSpeedMultiplier() {
        return 10;
    }
}
```

Register it through the existing block registry during `onLoad()`:

```java
CustomBlockRegistry.getInstance().register(MithrilFurnace.class);
```

The registry installs the shared server-side furnace block entity before it
registers the first furnace block. A custom tile class and a separate lit block
identifier are not needed.

The built-in `FurnacePermutationResolver` automatically:

- Declares cardinal placement with the correct 180-degree offset.
- Marks the custom block as interactable for the Bedrock client.
- Generates the eight direction/lit permutations.
- Rotates the full-block geometry for north, south, east and west.
- Switches the front texture when the furnace starts or stops burning.
- Emits the configured light level only while the `lit` state is true.
- Emits native smoke and flame particles from the oriented front face.
- Gives the inventory item a stable, unlit visual and state.

The default texture prefix is the identifier path. The example therefore uses:

- `mithril_furnace_side`
- `mithril_furnace_top`
- `mithril_furnace_front_off`
- `mithril_furnace_front_on`

Override `getTexturePrefix()`, an individual texture getter, or
`getBottomTexture()` when the resource pack follows another convention.
`getBurningLightLevel()` defaults to 13 and accepts 0 through 15.
`getCookingSpeedMultiplier()` accepts 1 through 200.
Override `displaysBurningParticles()` to disable the server-side particles.

The speed multiplier shortens cooking time while retaining the complete fuel
duration, matching an upgraded furnace rather than vanilla blast-furnace fuel
consumption. PNX still owns the actual tick loop and fires its native
`FurnaceBurnEvent` and `FurnaceSmeltEvent`.

`createFurnaceProperties()` must be used, or the class must explicitly declare
both `minecraft:cardinal_direction` and `lit`. Registration fails early if the
states, speed, light level, or texture keys are invalid.

## Traits: before and after

A Bedrock trait derives states automatically during placement or when
neighboring blocks change.

### Before: manual PNX NBT

PNX does not expose typed methods for every trait. The structure must be
injected through `customBuild()`, while the block properties are kept in sync
manually:

```java
private static CompoundTag cardinalPlacementTrait() {
    return new CompoundTag()
        .putString("name", "minecraft:placement_direction")
        .putCompound(
            "enabled_states",
            new CompoundTag().putByte("cardinal_direction", (byte) 1)
        );
}

@Override
public CustomBlockDefinition getDefinition() {
    return CustomBlockDefinition.builder(this).customBuild(root -> {
        ListTag<CompoundTag> traits = new ListTag<>(Tag.TAG_Compound);
        traits.add(cardinalPlacementTrait());
        root.putList("traits", traits);
    });
}
```

### After: typed ToolBox trait

The trait also supplies the PNX property types that must be declared:

```java
public final class RubyPillar extends Block
    implements ToolBoxBlock, ExtraBlockComponentsInterface {

    private static final PlacementDirectionTrait PLACEMENT_TRAIT =
        PlacementDirectionTrait.cardinal();

    public static final BlockProperties PROPERTIES = new BlockProperties(
        "example:ruby_pillar",
        PLACEMENT_TRAIT.getProvidedPropertiesArray()
    );

    public RubyPillar(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public void defineBlockComponents(BlockBuilder builder) {
        builder.addTrait(PLACEMENT_TRAIT);
    }
}
```

| Trait | Provided states |
| --- | --- |
| `ConnectionTrait` | North, south, west, and east connections |
| `MultiBlockTrait` | Part index for a vertical block containing 2 to 4 parts |
| `PlacementDirectionTrait` | Cardinal direction, full facing direction, or direction with corners |
| `PlacementPositionTrait` | Touched face and/or vertical half |
| `RawBlockTrait` | Arbitrary NBT without automatically provided properties |

`BlockBuilder` rejects the definition if a property required by a trait is
missing from `PROPERTIES`, or if its type and values do not match.

## Server ticks and sensors

`TickComponent` describes the Bedrock component. `setTickSettings()` also
configures server-side PNX ticking:

```java
@Override
public void defineBlockComponents(BlockBuilder builder) {
    builder.addComponent(new TickComponent(20, 40, true));
    builder.setTickSettings(20, 40, true);
    builder.setStepSensor(true);
}
```

When `setStepSensor(true)` is enabled, override `onEntityStepOn()` and
`onEntityStepOff()` in the block to handle the server-side events.

## Crop with a custom seed item

When the block and its item share the same identifier, use the linked
registration overload. It gives the item its own runtime ID and prevents PNX
from creating a competing `ItemBlock`.

### Onion block

```java
public final class OnionBlock extends BlockCrops implements ToolBoxBlock {
    public static final String IDENTIFIER = "example:onion";

    public static final BlockProperties PROPERTIES = new BlockProperties(
        IDENTIFIER,
        CommonBlockProperties.GROWTH
    );

    public OnionBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item tool) {
        Item onion = toItem();
        onion.setCount(
            isFullyGrown() ? FortuneDropHelper.binomial(tool, 1) : 1
        );
        return new Item[]{onion};
    }

    @Override
    public Item toItem() {
        return new OnionItem();
    }
}
```

### Onion item

The linked item must currently use the Legacy format and return its block from
`getBlock()`:

```java
@LegacyItem
public final class OnionItem extends Item
    implements LegacyExtraComponentsInterface {

    public OnionItem() {
        super(OnionBlock.IDENTIFIER);
    }

    @Override
    public Block getBlock() {
        return new OnionBlock(null);
    }

    @Override
    public void defineLegacyComponent(LegacyItemBuilder builder) {
        builder.addComponent(SeedComponent.fromBlocks(
            new OnionBlock(null),
            new BlockFarmland()
        ));
    }
}
```

Register both classes together:

```java
CustomBlockRegistry.getInstance().register(
    OnionBlock.class,
    OnionItem.class
);
```

Do not register `OnionItem` again through `CustomItemRegistry`; the linked
overload has already done so. The resource pack supplies `onion` for the item
and `onion_0` through `onion_7` for the crop.

## Resource pack

For `example:ruby_block`, provide at least:

- The `ruby_block` texture key in `textures/terrain_texture.json`.
- The PNG file referenced by that key.
- `tile.example:ruby_block.name=Ruby Block` in `texts/en_US.lang`, or the
  corresponding language file.
- Any custom geometry referenced by `GeometryComponent`.
- One texture per permutation when their texture names differ.

ToolBox registers the behavior and network definition, but it does not
generate the resource pack.

## Inspecting registered blocks

```java
RegisteredBlockData data = CustomBlockRegistry.getInstance().get(
    "example:ruby_block"
);

Map<String, RegisteredBlockData> blocks =
    CustomBlockRegistry.getInstance().getAll();
```

`RegisteredBlockData` exposes the model block instance, its class, the final
`BlockBuilder`, and the `CustomBlockDefinition` sent to PNX.

## Common errors

- `Block class must declare ... PROPERTIES`: the public static final field is
  missing or has the wrong type.
- `getProperties() must return ... PROPERTIES`: the method returns a different
  instance.
- `must have a public constructor accepting BlockState`: the required
  constructor is not public.
- `Identifier must be namespaced`: use `namespace:name`.
- `cannot use the minecraft namespace`: use your plugin's namespace.
- `must declare the trait property`: add the trait's provided properties to
  `PROPERTIES`.
- Purple/black texture: the expected texture key is missing from the resource
  pack.
- Two items for one block: use the linked block/item overload and do not
  perform a second manual registration.
