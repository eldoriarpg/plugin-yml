# plugin-yml

Gradle plugins that generate the plugin description file of a Minecraft plugin from the Gradle project:
`plugin.yml` for Bukkit, `paper-plugin.yml` for Paper, `bungee.yml` for BungeeCord and `nukkit.yml` for Nukkit.
Name, version and description come from the project, everything else from a small DSL, and the result is validated
before it is written.

This is the continuation of [Minecrell's plugin-yml](https://github.com/Minecrell/plugin-yml).

[![Release](https://img.shields.io/gradle-plugin-portal/v/de.eldoria.plugin-yml.paper?label=Release&style=for-the-badge)][release]
[![Snapshot](https://img.shields.io/nexus/s/de.eldoria/plugin-yml?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][snapshot]

## Requirements

Gradle running on Java 21 or newer (0.9.0 and older: Java 17).

## Usage

```kotlin
plugins {
    java
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
}
```

Use `de.eldoria.plugin-yml.bukkit`, `de.eldoria.plugin-yml.bungee` or `de.eldoria.plugin-yml.nukkit` for the other
platforms. Releases are on the Gradle Plugin Portal; snapshots are on EldoNexus
(`https://eldonexus.de/repository/maven-public/` in `pluginManagement`).

Documentation: https://docs.eldoria.de/plugin-yml

[release]: https://plugins.gradle.org/plugin/de.eldoria.plugin-yml.paper
[snapshot]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Feldoria%2Fplugin-yml
