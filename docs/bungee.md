---
sidebar_position: 4
title: BungeeCord
---

# BungeeCord (bungee.yml)

Use `de.eldoria.plugin-yml.bungee` for a BungeeCord plugin. It writes `bungee.yml`.

## Full example

Kotlin DSL:

```kotlin
plugins {
    java
    id("de.eldoria.plugin-yml.bungee") version "0.9.0"
}

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    bungeeLibrary("com.google.code.gson:gson:2.10.1")
}

bungee {
    main = "com.example.testplugin.TestPlugin"
    author = "Notch"
    depends = setOf("Yamler")
    softDepends = setOf("ServerListPlus")
}
```

Groovy DSL:

```groovy
plugins {
    id 'java'
    id 'de.eldoria.plugin-yml.bungee' version '0.9.0'
}

version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    bungeeLibrary 'com.google.code.gson:gson:2.10.1'
}

bungee {
    main = 'com.example.testplugin.TestPlugin'
    author = 'Notch'
    depends = ['Yamler']
    softDepends = ['ServerListPlus']
}
```

This produces:

```yaml
name: TestPlugin
main: com.example.testplugin.TestPlugin
version: 1.0.0
author: Notch
depends:
  - Yamler
softDepends:
  - ServerListPlus
libraries:
  - com.google.code.gson:gson:2.10.1
```

## Properties

Every property is optional unless the table says otherwise.

| Property | Type | Default | YAML key |
|---|---|---|---|
| `name` | `String` | project name | `name` |
| `main` | `String` | none, **required** | `main` |
| `version` | `String` | project version | `version` |
| `description` | `String` | project description | `description` |
| `author` | `String` | `author` project property | `author` |
| `depends` | `Set<String>` | none | `depends` |
| `softDepends` | `Set<String>` | none | `softDepends` |
| `libraries` | `List<String>` | none | `libraries` |
| `generateLibrariesJson` | `Boolean` | `false` | not written, see [Libraries](./libraries.md) |
| `extra` | `Map<String, Any>` | empty | top-level keys, see [Additional values](#additional-values) |

BungeeCord has no `website`, commands or permissions in its description file, so the block has none either.

The `libraries` list is filled for you from the `library` and `bungeeLibrary` dependencies. Entries you set by hand stay and the resolved ones are added after them. See [Libraries](./libraries.md).

## Additional values

Keys that Plugin YML does not know go into `extra`. Every entry is written as a top-level key of `bungee.yml`, and values can be strings, numbers, booleans, lists and nested maps:

```kotlin
bungee {
    extra.put("my-tool", mapOf("channels" to listOf("release", "beta")))
}
```

```groovy
bungee {
    extra = ['my-tool': [channels: ['release', 'beta']]]
}
```

A key may not be one of the YAML keys in the [Properties](#properties) table, whether that property is set or not. [Additional values](./bukkit.md#additional-values) on the Bukkit page has the details.

## Checks

The plugin only checks that a name and a main class are set. See [Validation](./validation.md#bungeecord).
