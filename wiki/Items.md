# Items

PNX-ToolBox registers legacy and data-driven custom items, allocates runtime IDs, generates common component data and places items in the creative inventory.

## Basic data-driven item

Create a public item class with a public no-argument constructor and annotate its format:

```java
@DataDrivenItem
public final class RubySword extends Item {
    public RubySword() {
        super("example:ruby_sword");
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 8;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return 1561;
    }
}
```

Register it during plugin loading:

```java
@Override
public void onLoad() {
    CustomItemRegistry.getInstance().register(RubySword.class);
}
```

The registry allocates the runtime ID and derives the icon, display name key, stack size, hand rendering, attack damage, mining speed, durability, enchantment slot and other supported defaults from the item.

The expected resource-pack texture path is the identifier path. For `example:ruby_sword`, use `ruby_sword` in the resource pack.

## Custom tools and tiers

Extend `TieredItemTool` to keep the PowerNukkitX behavior and the generated
Bedrock components in sync. A tier contains its harvest level, mining speed,
base attack damage, durability and enchantability:

```java
@DataDrivenItem
public final class RubyPickaxe extends TieredItemTool {
    private static final ToolTier RUBY = new ToolTier(
        8,    // Harvest level
        10,   // Mining speed
        9,    // Base attack damage (sword)
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

`ToolTier` also provides the `WOODEN`, `GOLD`, `STONE`, `COPPER`, `IRON`,
`DIAMOND` and `NETHERITE` constants. For pickaxes, axes and shovels, the toolbox
generates `minecraft:digger` with the appropriate block tags and enables the
Efficiency enchantment. Existing `ItemTool` implementations receive the same
automatic component from their vanilla tier.

Attack damage is derived from the tier and tool type: swords use the base
damage, axes use `base - 1`, pickaxes and hoes use `base - 2`, and shovels and
spears use `base - 3`. Reduced values never go below `1`. Extend
`TieredItemSpear` instead of `TieredItemTool` for a spear so its native PNX
lunge and stab behavior is preserved.

Implement `DataDrivenExtraComponentsInterface` and add another
`DiggerComponent` only when an item needs custom block rules. The explicitly
added component replaces the generated default.

## Adding components and properties

Implement `DataDrivenExtraComponentsInterface` when an item needs additional data:

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
        builder.addComponent(new TagsComponent(
            "example:gem",
            "example:ruby"
        ));
    }
}
```

Components and properties are keyed by their Bedrock identifier. Adding another value with the same identifier replaces the previous value, which makes automatic defaults easy to override.

For legacy items, use `@LegacyItem`, implement `LegacyExtraComponentsInterface`, and add legacy components to `LegacyItemBuilder`.

## Creative inventory

Creative placement is automatic for:

- Armor: helmet, chestplate, leggings and boots.
- Tools: sword, pickaxe, axe, shovel and hoe.
- Other equipment: bows, crossbows, shears, spears, tridents, maces and shields.
- Ores, seeds, saplings and crops.
- Mob spawn eggs.
- Smithing templates.

The detector uses PNX item behavior and block types first, then common identifier suffixes such as `_ore`, `_seeds`, `_sapling`, `_spawn_egg` and `_smithing_template`.

Override the detected placement with `@CreativeInventoryInfo`:

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

Hide an item completely:

```java
@CreativeInventoryInfo(hidden = true)
```

Use `customGroup = "My Custom Group"` for a custom group that has already been registered through the PNX creative-group API. Do not set both `group` and `customGroup` on the same annotation.

## Before: native PowerNukkitX

With the native custom-item builder, the item definition repeats client-facing properties and creative placement:

```java
public final class RubySword extends Item implements CustomItem {
    public RubySword() {
        super("example:ruby_sword");
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.simpleBuilder(this)
            .name("Ruby Sword")
            .texture("ruby_sword")
            .maxStackSize(1)
            .damage(8)
            .durability(1561)
            .handEquipped(true)
            .creativeCategory(CreativeCategory.EQUIPMENT)
            .creativeGroup(CreativeGroup.SWORD)
            .build();
    }
}
```

```java
try {
    Registries.ITEM.registerCustomItem(this, RubySword.class);
} catch (RegisterException exception) {
    throw new IllegalStateException("Unable to register Ruby Sword", exception);
}
```

## After: PNX-ToolBox

PNX-ToolBox derives the same common data from the item itself. The sword behavior also selects `Equipment → Sword` automatically:

```java
@DataDrivenItem
public final class RubySword extends Item {
    public RubySword() {
        super("example:ruby_sword");
    }

    @Override public boolean isSword() {
        return true;
    }

    @Override public int getAttackDamage() {
        return 8;
    }

    @Override public int getMaxStackSize() {
        return 1;
    }

    @Override public boolean canTakeDamage() {
        return true;
    }

    @Override public int getMaxDurability() {
        return 1561;
    }
}
```

```java
CustomItemRegistry.getInstance().register(RubySword.class);
```

You only describe server-side item behavior. The toolbox generates the common definition, registers the runtime entry and handles creative placement.
