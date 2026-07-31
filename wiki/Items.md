# Items

PNX-ToolBox registers custom PowerNukkitX items, allocates their runtime IDs,
builds their Bedrock properties/components, and places them in the creative
inventory.

This guide uses the identifier `example:ruby`. Always replace `example` with
your plugin's namespace.

## Before: native PNX item

With PowerNukkitX alone, a custom item implements `CustomItem` and repeats
several values in `getDefinition()` that are already available from the class:

```java
public final class RubyItem extends Item implements CustomItem {
    public static final String IDENTIFIER = "example:ruby";

    public RubyItem() {
        super(IDENTIFIER);
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.simpleBuilder(this)
            .name("Ruby")
            .texture("ruby")
            .maxStackSize(64)
            .handEquipped(false)
            .canDestroyInCreative(true)
            .creativeCategory(CreativeCategory.ITEMS)
            .build();
    }
}
```

Native registration receives the plugin instance and requires explicit
`RegisterException` handling:

```java
@Override
public void onLoad() {
    try {
        Registries.ITEM.registerCustomItem(this, RubyItem.class);
    } catch (RegisterException exception) {
        throw new IllegalStateException(
            "Unable to register RubyItem",
            exception
        );
    }
}
```

## After: the same item with ToolBox

With ToolBox, the annotation selects the format and the class only keeps its
server-side behavior:

```java
@DataDrivenItem
public final class RubyItem extends Item {
    public static final String IDENTIFIER = "example:ruby";

    public RubyItem() {
        super(IDENTIFIER);
    }
}
```

```java
@Override
public void onLoad() {
    CustomItemRegistry.getInstance().register(RubyItem.class);
}
```

ToolBox derives the `ruby` icon, the `item.example:ruby.name` translation key,
the stack size, hand-related properties, and creative placement, then performs
the complete PNX registration.

## Choosing a format

Every item must use exactly one format annotation:

| Format | Annotation | Recommended use |
| --- | --- | --- |
| Data-driven | `@DataDrivenItem` | General items, tools, armor, food, projectiles, and modern Bedrock components |
| Legacy | `@LegacyItem` | Legacy components and custom items linked to blocks, especially seeds |

Never place both annotations on the same class. `CustomItemRegistry` rejects a
class that has neither annotation.

## Item class requirements

- The class must be public and extend `Item` or one of its subclasses.
- It must have a public no-argument constructor.
- Its constructor must provide a `namespace:name` identifier.
- The `minecraft:` namespace is reserved and rejected.
- It must use either `@DataDrivenItem` or `@LegacyItem`.
- It must be registered only once, preferably during `onLoad()`.
- A Data-driven class cannot implement `LegacyExtraComponentsInterface`, and
  a Legacy class cannot implement `DataDrivenExtraComponentsInterface`.

Class-based registration allows ToolBox and PNX to recreate the item later by
calling its public constructor.

## Automatically generated values

### Data-driven format

ToolBox inspects the item's PNX type and methods:

| Property or component | Source |
| --- | --- |
| Icon | Identifier path after `:` |
| Display name | `item.<identifier>.name` |
| Stack size | `getMaxStackSize()` |
| Hand rendering | `isTool()` |
| Damage | `getAttackDamage()` |
| Mining speed | PNX tier or `ToolTierProvider` |
| Digging rules | Pickaxe, axe, or shovel behavior |
| Durability | `canTakeDamage()`, `isUnbreakable()`, and `getMaxDurability()` |
| Enchanting | Tool/armor type and `getEnchantAbility()` |
| Fire resistance | `isLavaResistant()` |
| Fuel | `getFuelTime()` |
| Armor | `isArmor()`, equipment slot, and protection points |
| Block placement | Value returned by `getBlock()` |
| Dyeing | `ItemDye` |
| Food | `isEdible()`, nutrition, and saturation |
| Use animation | Potion, food, bow, crossbow, shield, or spear type |

Pickaxes, axes, and shovels also receive a `DiggerComponent` with suitable
block tags and Efficiency enchantment support.

### Legacy format

The automatic Legacy definition is intentionally smaller:

- `MaxStackSizeComponent` from `getMaxStackSize()`.
- `StackByDataComponent(false)`.
- `HandEquippedComponent` from `isTool()`.

Item-specific components, such as `SeedComponent`, are added through
`LegacyExtraComponentsInterface`.

## Adding or replacing Data-driven components

Implement `DataDrivenExtraComponentsInterface`:

```java
@DataDrivenItem
public final class GlowingRuby extends Item
    implements DataDrivenExtraComponentsInterface {

    public GlowingRuby() {
        super("example:glowing_ruby");
    }

    @Override
    public void defineDataDrivenComponent(DataDrivenItemBuilder builder) {
        builder.addProperty(new GlintProperty(true));
        builder.addProperty(new RarityProperty(RarityProperty.RARE));
        builder.addComponent(new TagsComponent(
            "example:gem",
            "example:ruby"
        ));
        builder.addComponent(new CooldownComponent("ruby", 1.5f));
    }
}
```

Components and properties are indexed by their Bedrock identifier. An
explicitly added value therefore replaces the automatic value with the same
identifier:

```java
builder.addProperty(new MaxStackSizeProperty(16));
builder.removeComponent(ItemComponentNames.FIRE_RESISTANT);
```

Creative category and group values are applied after custom components. Use
`@CreativeInventoryInfo` to change them instead of manually adding
`CreativeCategoryProperty` or `CreativeGroupProperty`.

## Adding or replacing Legacy components

```java
@LegacyItem
public final class LegacyRuby extends Item
    implements LegacyExtraComponentsInterface {

    public LegacyRuby() {
        super("example:legacy_ruby");
    }

    @Override
    public void defineLegacyComponent(LegacyItemBuilder builder) {
        builder.addComponent(new FoilComponent(true));
        builder.addComponent(new MaxStackSizeComponent(16));
    }
}
```

The last component added also replaces an automatic Legacy component with the
same identifier.

## Component catalog

### Data-driven components

| Category | Available components |
| --- | --- |
| Display and equipment | `DisplayNameComponent`, `DyeableComponent`, `WearableItemComponent`, `SwingDurationComponent`, `SwingSoundsComponent` |
| Use behavior | `CooldownComponent`, `FoodComponent`, `UseModifiersComponent`, `CompostableComponent`, `FuelComponent` |
| Durability and repair | `DurabilityComponent`, `DurabilitySensorComponent`, `RepairableComponent`, `DamageAbsorptionComponent`, `FireResistantComponent` |
| Tools and combat | `DiggerComponent`, `EnchantableComponent`, `KineticWeaponComponent`, `PiercingWeaponComponent` |
| Projectiles | `ProjectileComponent`, `ShooterComponent`, `ThrowableComponent` |
| Placement | `BlockPlacerItemComponent`, `EntityPlacerComponent` |
| Storage | `BundleInteractionComponent`, `StorageItemComponent`, `StorageWeightLimitComponent`, `StorageWeightModifierComponent` |
| Miscellaneous | `RecordComponent`, `TagsComponent` |

Available helper types include `Ammunition`, `BlockDescriptor`, `CooldownType`,
`DamageCause`, `DestroySpeed`, `DurabilityThreshold`, `EnchantSlot`,
`EquipmentSlot`, `ItemRange`, and `RepairItem`.

### Legacy components

| Component | Purpose |
| --- | --- |
| `BlockRenderComponent` | Uses a block's item rendering |
| `FoilComponent` | Enables the Legacy foil effect |
| `HandEquippedComponent` | Renders the item as hand-equipped |
| `MaxDamageComponent` | Sets maximum Legacy damage/durability |
| `MaxStackSizeComponent` | Sets maximum stack size |
| `SeedComponent` | Defines the crop and valid planting surfaces |
| `StackByDataComponent` | Separates stacks according to item data |
| `UseDurationComponent` | Sets Legacy use duration |

## Data-driven property catalog

| Category | Available properties |
| --- | --- |
| Inventory and rendering | `IconProperty`, `HoverTextColorProperty`, `RarityProperty`, `GlintProperty`, `FoilProperty` |
| Stacking and lifetime | `MaxStackSizeProperty`, `StackedByDataProperty`, `ShouldDespawnProperty` |
| Hands and interaction | `HandEquippedProperty`, `AllowOffHandProperty`, `InteractButtonProperty` |
| Use behavior | `UseAnimationProperty`, `UseDurationProperty`, `LiquidClippedProperty` |
| Combat and mining | `DamageProperty`, `MiningSpeedProperty`, `CanDestroyInCreativeProperty` |
| Placement | `BlockProperty` |
| Creative inventory | `CreativeCategoryProperty`, `CreativeGroupProperty` |

## Tools: before and after

### Before: repeated native PNX data

A native PNX tool must manually keep its server methods and client definition
synchronized:

```java
public final class RubyPickaxe extends ItemCustomTool {
    public RubyPickaxe() {
        super("example:ruby_pickaxe");
    }

    @Override public boolean isPickaxe() { return true; }
    @Override public int getTier() { return 8; }
    @Override public int getMaxDurability() { return 2000; }
    @Override public int getEnchantAbility() { return 18; }
    @Override public int getAttackDamage() { return 7; }

    @Override
    public CustomItemDefinition getDefinition() {
        CustomItemDefinition.ToolBuilder builder =
            CustomItemDefinition.toolBuilder(this);

        builder.name("Ruby Pickaxe");
        builder.texture("ruby_pickaxe");
        builder.maxStackSize(1);
        builder.damage(7);
        builder.durability(2000);
        builder.enchantable(ItemEnchantSlot.PICKAXE, 18);
        builder.speed(10);
        builder.creativeCategory(CreativeCategory.EQUIPMENT);
        builder.creativeGroup(CreativeGroup.PICKAXE);
        return builder.build();
    }
}
```

### After: one `ToolTier`

```java
@DataDrivenItem
public final class RubyPickaxe extends TieredItemTool {
    private static final ToolTier RUBY = new ToolTier(
        8,    // Harvest level
        10,   // Mining speed
        9,    // Base sword damage
        2000, // Durability
        18    // Enchantability
    );

    public RubyPickaxe() {
        super("example:ruby_pickaxe", RUBY);
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }
}
```

`TieredItemTool` keeps the tier, speed, durability, enchantability, and
server-side damage synchronized with the generated Bedrock data.

The predefined `WOODEN`, `GOLD`, `STONE`, `COPPER`, `IRON`, `DIAMOND`, and
`NETHERITE` tiers are also available. Extend `TieredItemSpear` for a spear that
must preserve the native PNX lunge and stab behavior.

Damage starts from `baseAttackDamage`:

- Sword: base value.
- Axe: base minus 1.
- Pickaxe and hoe: base minus 2.
- Shovel and spear: base minus 3.
- The result never falls below 1.

Add a custom `DiggerComponent` only when the automatic tags do not match the
blocks that the tool should mine.

## Creative inventory

Automatic detection covers:

- Helmets, chestplates, leggings, and boots.
- Swords, pickaxes, axes, shovels, and hoes.
- Bows, crossbows, shears, spears, tridents, maces, and shields.
- Ores, seeds, saplings, and crops.
- Spawn eggs.
- Smithing templates.

The resolver first uses PNX behavior, then checks common suffixes such as
`_ore`, `_seeds`, `_sapling`, `_spawn_egg`, and `_smithing_template`.

Override the detected placement with:

```java
@DataDrivenItem
@CreativeInventoryInfo(
    category = CreativeCategory.NATURE,
    group = CreativeGroup.ORE
)
public final class RubyOreItem extends Item {
    public RubyOreItem() {
        super("example:ruby_ore");
    }
}
```

Hide the item completely with:

```java
@CreativeInventoryInfo(hidden = true)
```

Use `customGroup = "My Group"` for a custom group that has already been
registered through PNX. Do not set both `group` and `customGroup`.

## Seed item linked to a block

An item linked to a custom block must:

- Use `@LegacyItem`.
- Share exactly the same identifier as the block.
- Return that block from `getBlock()`.
- Add its `SeedComponent`.
- Be registered through the `CustomBlockRegistry` overload.

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

```java
CustomBlockRegistry.getInstance().register(
    OnionBlock.class,
    OnionItem.class
);
```

Do not call `CustomItemRegistry.register(OnionItem.class)` afterwards because
the item has already been registered with its block. The complete Onion block
example is available in [[Blocks]].

## Resource pack

For `example:ruby`, provide at least:

- The `ruby` key in `textures/item_texture.json`.
- The PNG file referenced by that key.
- `item.example:ruby.name=Ruby` in `texts/en_US.lang`, or the corresponding
  language file.
- Any models, attachables, or animations required by advanced components.

The default Data-driven icon key is the identifier path after `:`. For
`example:ruby_pickaxe`, ToolBox therefore expects `ruby_pickaxe`.

ToolBox generates and registers the network definition, but it does not
generate the resource pack.

## Inspecting registered items

```java
RegisteredItemData data = CustomItemRegistry.getInstance().get(
    "example:ruby"
);
```

`RegisteredItemData` exposes the model item, its class, runtime ID, format, and
component data sent to the client.

## Common errors

- `Unsupported item format`: add `@DataDrivenItem` or `@LegacyItem`.
- `cannot use both`: remove one of the two annotations.
- `must have a public no-argument constructor`: add the required public
  constructor.
- `Identifier must be namespaced`: use `namespace:name`.
- `cannot use the minecraft namespace`: use your plugin's namespace.
- `cannot define legacy/data-driven components`: the extension interface does
  not match the format annotation.
- `is already registered`: the item was registered twice.
- Rejected block item: verify the Legacy format, shared identifier, and
  `getBlock()` implementation.
- Purple/black texture: the expected key is missing from the resource pack.
