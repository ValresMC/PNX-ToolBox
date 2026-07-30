# PNX-ToolBox

Toolbox for PowerNukkitX plugins: manager lifecycle, RCON and a typed command framework.

The command framework supports nested sub-commands, typed arguments, reusable rules,
automatic permissions, Bedrock overloads/autocompletion and live command refresh.
Rule and argument errors are configurable through `command-messages-config.yml` and
served from an immutable runtime cache that can be refreshed without a restart.

See [the command guide](wiki/commands.md) for a complete example and the available API.
