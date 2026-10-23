---
sidebar_position: 1
title: Plugin YML
---

# Plugin YML

Plugin YML is a family of Gradle plugins. It writes the plugin description file of your Minecraft plugin for you, straight from your build script:

- `plugin.yml` for Bukkit, Spigot and Paper plugins
- `paper-plugin.yml` for Paper plugins
- `bungee.yml` for BungeeCord plugins
- `nukkit.yml` for Nukkit plugins

You no longer keep a YAML file by hand next to your code. Name, version and description come from your Gradle project. Everything else you set in a small DSL. Before the file is written, the plugin checks it and stops the build with a clear message when something is wrong. It can also list your runtime libraries for the server to download.

Plugin YML continues the original plugin-yml by Minecrell. The source lives at [github.com/eldoriarpg/plugin-yml](https://github.com/eldoriarpg/plugin-yml).

## Platforms

There is one Gradle plugin per platform. The current version of all four is `0.9.0`.

| Platform | Plugin id | Generated file | Build script block | Guide |
|---|---|---|---|---|
| Bukkit, Spigot, Paper | `de.eldoria.plugin-yml.bukkit` | `plugin.yml` | `bukkit {}` | [Bukkit](./bukkit.md) |
| Paper (Paper plugins) | `de.eldoria.plugin-yml.paper` | `paper-plugin.yml` | `paper {}` | [Paper](./paper.md) |
| BungeeCord | `de.eldoria.plugin-yml.bungee` | `bungee.yml` | `bungee {}` | [BungeeCord](./bungee.md) |
| Nukkit | `de.eldoria.plugin-yml.nukkit` | `nukkit.yml` | `nukkit {}` | [Nukkit](./nukkit.md) |

The plugins are published on the Gradle Plugin Portal, for example [de.eldoria.plugin-yml.bukkit](https://plugins.gradle.org/plugin/de.eldoria.plugin-yml.bukkit). Gradle finds them there without any extra repository.

## Add the plugin

Apply the plugin for your platform next to the `java` plugin. The Kotlin JVM plugin applies `java` for you, so it works as well.

Kotlin DSL (`build.gradle.kts`):

```kotlin
plugins {
    java
    id("de.eldoria.plugin-yml.bukkit") version "0.9.0"
}
```

Groovy DSL (`build.gradle`):

```groovy
plugins {
    id 'java'
    id 'de.eldoria.plugin-yml.bukkit' version '0.9.0'
}
```

Swap `bukkit` for `paper`, `bungee` or `nukkit` to target another platform. You can apply more than one platform plugin in the same project. Each one gets its own block, task and file.

The plugins are compiled for Java 17, so Gradle has to run on Java 17 or newer.

## A first plugin.yml

The smallest useful setup only names your main class:

```kotlin
bukkit {
    main = "com.example.myplugin.MyPlugin"
    apiVersion = "1.21"
}
```

Build the project and the jar contains a `plugin.yml` like this:

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.myplugin.MyPlugin
api-version: "1.21"
```

## Values taken from your project

You do not have to repeat what Gradle already knows. When you leave one of these values unset, Plugin YML fills it in:

| Value | Taken from | Platforms |
|---|---|---|
| `name` | the project name | all |
| `version` | the project version | all |
| `description` | the project description | all |
| `author` | the `author` project property | all |
| `website` | the `url` project property | Bukkit, Paper, Nukkit |

Project properties usually live in `gradle.properties`:

```properties
author=Notch
url=https://example.com
```

A value you set yourself in the block always wins.

:::warning
If your project has no `version`, Gradle reports it as `unspecified`, and that word ends up as the plugin version. Set `version` in your build script.
:::

Values that are empty, such as an unset property or an empty list, are left out of the generated file.

## Generated files and tasks

Each platform plugin registers one task in the `plugin-yml` task group:

| Platform | Task | Output folder |
|---|---|---|
| Bukkit | `generateBukkitPluginDescription` | `build/generated/plugin-yml/Bukkit/` |
| Paper | `generatePaperPluginDescription` | `build/generated/plugin-yml/Paper/` |
| BungeeCord | `generateBungeePluginDescription` | `build/generated/plugin-yml/Bungee/` |
| Nukkit | `generateNukkitPluginDescription` | `build/generated/plugin-yml/Nukkit/` |

The output folder is added to the resources of the `main` source set. So the description file lands at the root of your jar, and the task runs on its own whenever Gradle processes resources. You never call it by hand. To look at the file without building a jar, run the task directly:

```bash
./gradlew generateBukkitPluginDescription
```

When you turn on `generateLibrariesJson`, the same folder also gets a `<platform>-libraries.json`. See [Libraries](./libraries.md).

## Moving from Minecrell's plugin-yml

Plugin YML started as a fork of `net.minecrell.plugin-yml`. Only the plugin ids changed:

| Before | Now |
|---|---|
| `net.minecrell.plugin-yml.bukkit` | `de.eldoria.plugin-yml.bukkit` |
| `net.minecrell.plugin-yml.paper` | `de.eldoria.plugin-yml.paper` |
| `net.minecrell.plugin-yml.bungee` | `de.eldoria.plugin-yml.bungee` |
| `net.minecrell.plugin-yml.nukkit` | `de.eldoria.plugin-yml.nukkit` |

The classes still live in the `net.minecrell.pluginyml` package. Imports such as `net.minecrell.pluginyml.bukkit.BukkitPluginDescription` keep working, and the blocks in your build script stay the same. Change the id and the version in `plugins {}` and you are done.

## Next steps

- Pick your platform: [Bukkit](./bukkit.md), [Paper](./paper.md), [BungeeCord](./bungee.md) or [Nukkit](./nukkit.md).
- Let the server download your dependencies: [Libraries](./libraries.md).
- Understand a failed build: [Validation](./validation.md).
