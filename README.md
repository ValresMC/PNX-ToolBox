# PNX-ToolBox

Toolbox for PowerNukkitX plugins: manager lifecycle, RCON and a typed command framework.

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

See [the wiki](wiki/Home.md) for guides and complete examples.
