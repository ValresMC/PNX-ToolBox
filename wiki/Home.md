# PNX-ToolBox

PNX-ToolBox is a developer toolkit for [PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX). Its goal is simple: remove repetitive setup code so plugin developers can spend more time building gameplay features.

PowerNukkitX already provides the low-level APIs needed to create plugins. PNX-ToolBox builds on top of them with typed, reusable abstractions for the tasks that tend to be repeated in every project.

## Why use it?

PNX-ToolBox helps you:

- Build nested commands without manually parsing `String[] args`.
- Declare typed arguments, permissions, cooldowns and sender rules.
- Refresh command definitions and Bedrock autocompletion at runtime.
- Split a large plugin into managers with a predictable lifecycle.
- Register legacy and data-driven custom items with automatic defaults.
- Automatically place custom items in the correct creative category and group.
- Run an optional configurable RCON server.

The toolbox does not replace PowerNukkitX. It keeps the PNX types you already know—`Player`, `Item`, `PluginBase`, `CommandSender`, `Block`, and the registries—while reducing the boilerplate around them.

## Installation

1. Build or download the PNX-ToolBox JAR.
2. Put it in the server's `plugins` directory.
3. Add PNX-ToolBox as a dependency in your plugin description:

```yaml
name: ExamplePlugin
main: com.example.ExamplePlugin
version: "1.0.0"
api: ["3.0.0"]
depend: ["PNX-ToolBox"]
```

PNX-ToolBox targets Java 21 and the PowerNukkitX 3.0.0 plugin API.

## Documentation

- [[Commands]] — typed commands, subcommands, arguments, rules and live refresh.
- [[Managers]] — organize plugin services around a predictable lifecycle.
- [[Items]] — register custom items, add components and configure creative placement.

## Design philosophy

The library follows three principles:

1. **Sensible defaults:** common data is inferred from the PNX object whenever possible.
2. **Explicit overrides:** annotations and builders remain available when automatic behavior is not enough.
3. **PNX compatibility:** handlers receive native PNX objects instead of toolbox-specific replacements.

