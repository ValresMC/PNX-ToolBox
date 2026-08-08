# PNX-ToolBox

Toolbox for PowerNukkitX plugins: manager lifecycle, RCON and a typed command framework.

Interactive inventory menus are available through a Java-native `InventoryMenu` facade built
on PowerNukkitX's fake inventory and item-stack request APIs. Menus support shared live
contents, explicit transaction decisions, close callbacks and reusable subclasses without
requiring packet listeners or manual registration.

The command framework supports nested sub-commands, typed arguments, reusable rules,
automatic permissions, Bedrock overloads/autocompletion and live command refresh.
Rule and argument errors are configurable through `command-messages-config.yml` and
served from an immutable runtime cache that can be refreshed without a restart.

Custom items registered through `CustomItemRegistry` are automatically added to
the creative inventory. The toolbox detects armor, tools, ores, smithing
templates, seeds, saplings, crops and spawn eggs, then places them in the matching
Bedrock category and group. `@CreativeInventoryInfo` can override the detected
placement or hide an item.

Custom blocks registered through `CustomBlockRegistry` receive generated
Bedrock components from their PNX behavior. Crop permutations are automatic,
and reusable permutation resolvers can be registered for other block families.

Code-defined shaped and shapeless recipes can be assigned to custom crafting
tables through `CustomCraftRegistry`, without JSON or YAML recipe files.

See [the wiki](wiki/Home.md) for guides and complete examples.
