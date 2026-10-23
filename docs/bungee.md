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

BungeeCord has no `website`, commands or permissions in its description file, so the block has none either.

The `libraries` list is filled for you from the `library` and `bungeeLibrary` dependencies. Entries you set by hand stay and the resolved ones are added after them. See [Libraries](./libraries.md).

## Checks

The plugin only checks that a name and a main class are set. See [Validation](./validation.md#bungeecord).
