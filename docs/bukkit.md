---
sidebar_position: 2
title: Bukkit
---

# Bukkit, Spigot and Paper (plugin.yml)

Use `de.eldoria.plugin-yml.bukkit` for a classic plugin that runs on Bukkit, Spigot or Paper. It writes `plugin.yml`.

If you are building a Paper plugin with a `paper-plugin.yml`, use the [Paper](./paper.md) plugin instead.

## Full example

Kotlin DSL:

```kotlin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    id("de.eldoria.plugin-yml.bukkit") version "0.9.0"
}

version = "1.0.0"
description = "This is a test plugin"

repositories {
    mavenCentral()
}

dependencies {
    library("com.google.code.gson:gson:2.10.1")
    bukkitLibrary("org.apache.commons:commons-lang3:3.14.0")
}

bukkit {
    main = "com.example.testplugin.TestPlugin"
    apiVersion = "1.21"
    foliaSupported = true

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Notch", "Jeb")
    contributors = listOf("Dinnerbone")
    depend = listOf("WorldEdit")
    softDepend = listOf("Essentials")
    loadBefore = listOf("BrokenPlugin")
    prefix = "TEST"
    defaultPermission = BukkitPluginDescription.Permission.Default.OP
    provides = listOf("TestPluginOldName")

    commands {
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "/<command>"
            permissionMessage = "You may not test this command!"
        }
    }

    permissions {
        register("testplugin.*") {
            children = listOf("testplugin.test")
        }
        register("testplugin.test") {
            description = "Allows you to run the test command"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
```

Groovy DSL:

```groovy
plugins {
    id 'java'
    id 'de.eldoria.plugin-yml.bukkit' version '0.9.0'
}

version = '1.0.0'
description = 'This is a test plugin'

repositories {
    mavenCentral()
}

dependencies {
    library 'com.google.code.gson:gson:2.10.1'
    bukkitLibrary 'org.apache.commons:commons-lang3:3.14.0'
}

bukkit {
    main = 'com.example.testplugin.TestPlugin'
    apiVersion = '1.21'
    foliaSupported = true

    load = 'STARTUP'
    authors = ['Notch', 'Jeb']
    contributors = ['Dinnerbone']
    depend = ['WorldEdit']
    softDepend = ['Essentials']
    loadBefore = ['BrokenPlugin']
    prefix = 'TEST'
    defaultPermission = 'OP'
    provides = ['TestPluginOldName']

    commands {
        test {
            description = 'This is a test command!'
            aliases = ['t']
            permission = 'testplugin.test'
            usage = '/<command>'
            permissionMessage = 'You may not test this command!'
        }
    }

    permissions {
        'testplugin.*' {
            children = ['testplugin.test']
        }
        'testplugin.test' {
            description = 'Allows you to run the test command'
            setDefault('OP')
        }
    }
}
```

In Groovy, enum values are given as strings (`'STARTUP'`, `'OP'`). A permission's default uses `setDefault(...)`, because `default` is a reserved word in Groovy.

## Properties

Every property is optional unless the table says otherwise.

| Property | Type | Default | YAML key |
|---|---|---|---|
| `name` | `String` | project name | `name` |
| `version` | `String` | project version | `version` |
| `main` | `String` | none, **required** | `main` |
| `description` | `String` | project description | `description` |
| `apiVersion` | `String` | none | `api-version` |
| `load` | `PluginLoadOrder` | none | `load` |
| `author` | `String` | `author` project property | `author` |
| `authors` | `List<String>` | none | `authors` |
| `contributors` | `List<String>` | none | `contributors` |
| `website` | `String` | `url` project property | `website` |
| `depend` | `List<String>` | none | `depend` |
| `softDepend` | `List<String>` | none | `softdepend` |
| `loadBefore` | `List<String>` | none | `loadbefore` |
| `prefix` | `String` | none | `prefix` |
| `defaultPermission` | `Permission.Default` | none | `default-permission` |
| `provides` | `List<String>` | none | `provides` |
| `libraries` | `List<String>` | none | `libraries` |
| `foliaSupported` | `Boolean` | none | `folia-supported` |
| `paperPluginLoader` | `String` | none | `paper-plugin-loader` |
| `paperSkipLibraries` | `Boolean` | none | `paper-skip-libraries` |
| `generateLibrariesJson` | `Boolean` | `false` | not written, see [Libraries](./libraries.md) |
| `extra` | `Map<String, Any>` | empty | top-level keys, see [Additional values](#additional-values) |
| `commands` | container of commands | empty | `commands` |
| `permissions` | container of permissions | empty | `permissions` |

`PluginLoadOrder` is `BukkitPluginDescription.PluginLoadOrder` with the values `STARTUP` and `POSTWORLD`.

`Permission.Default` is `BukkitPluginDescription.Permission.Default`. Its values are written like this:

| Value | Written as |
|---|---|
| `TRUE` | `true` |
| `FALSE` | `false` |
| `OP` | `op` |
| `NOT_OP` | `'!op'` |

### API version

`apiVersion` is optional on Bukkit. When you set it, it has to be at least `1.13`. Patch versions such as `1.21.4` are accepted from `1.20.5` on, and the new version numbers such as `26.1` and `26.1.1` work too. The full rules are on the [Validation](./validation.md#api-version-rules) page.

### Libraries

The `libraries` list is filled for you from the `library` and `bukkitLibrary` dependencies. Entries you set by hand stay in the list and the resolved ones are added after them. See [Libraries](./libraries.md) for the details.

## Commands

Each command is registered by its name. In Kotlin use `register("name") { }`, in Groovy write the name followed by a block.

| Property | Type | YAML key |
|---|---|---|
| `description` | `String` | `description` |
| `aliases` | `List<String>` | `aliases` |
| `permission` | `String` | `permission` |
| `permissionMessage` | `String` | `permission-message` |
| `usage` | `String` | `usage` |

A command name and its aliases may not contain `:`.

## Permissions

Each permission is registered by its node, for example `testplugin.test`.

| Property | Type | YAML key |
|---|---|---|
| `description` | `String` | `description` |
| `default` | `Permission.Default` | `default` |
| `children` | `List<String>` | `children` |
| `childrenMap` | `Map<String, Boolean>` | `children` |

`children` and `childrenMap` fill the same `children` section. With `children` every child is set to `true`. Use `childrenMap` when you need to set a child to `false`:

```kotlin
permissions {
    register("testplugin.admin") {
        childrenMap = mapOf("testplugin.test" to true, "testplugin.other" to false)
    }
}
```

```groovy
permissions {
    'testplugin.admin' {
        childrenMap = ['testplugin.test': true, 'testplugin.other': false]
    }
}
```

Both write to the same place, so the one you set last wins.

## Generated file

The Kotlin example above produces this `plugin.yml` for a project called `TestPlugin`, with `author` and `url` set in `gradle.properties`:

```yaml
name: TestPlugin
version: 1.0.0
main: com.example.testplugin.TestPlugin
description: This is a test plugin
load: STARTUP
author: Notch
authors:
  - Notch
  - Jeb
contributors:
  - Dinnerbone
website: https://example.com
depend:
  - WorldEdit
prefix: TEST
provides:
  - TestPluginOldName
libraries:
  - org.apache.commons:commons-lang3:3.14.0
  - com.google.code.gson:gson:2.10.1
commands:
  test:
    description: This is a test command!
    aliases:
      - t
    permission: testplugin.test
    usage: /<command>
    permission-message: You may not test this command!
permissions:
  testplugin.*:
    children:
      testplugin.test: true
  testplugin.test:
    description: Allows you to run the test command
    default: op
api-version: "1.21"
softdepend:
  - Essentials
loadbefore:
  - BrokenPlugin
default-permission: op
folia-supported: true
```

## Additional values

Some plugins and tools read keys from `plugin.yml` that Plugin YML does not know. Put them into `extra`. Every entry is written as a top-level key of the file, after the values Plugin YML writes itself. Values can be strings, numbers, booleans, lists and nested maps. `extra` is available since Plugin YML 0.10.0.

[PluginUpdater](https://github.com/OakLoaf/PluginUpdater/wiki/PluginUpdater-Plugin#adding-support-for-pluginupdater), for example, finds your plugin through keys such as `modrinth-project-id`.

Kotlin DSL:

```kotlin
bukkit {
    main = "com.example.testplugin.TestPlugin"

    extra.put("modrinth-project-id", "djC8I9ui")
    extra.put("github-repo", "OakLoaf/LushRewards")
}
```

Groovy DSL:

```groovy
bukkit {
    main = 'com.example.testplugin.TestPlugin'

    extra = [
        'modrinth-project-id': 'djC8I9ui',
        'github-repo': 'OakLoaf/LushRewards'
    ]
}
```

Both write:

```yaml
name: TestPlugin
version: 1.0.0
main: com.example.testplugin.TestPlugin
modrinth-project-id: djC8I9ui
github-repo: OakLoaf/LushRewards
```

`extra.put(key, value)` adds one entry. `extra = ...` replaces all entries, in Kotlin with `extra = mapOf(...)`. Nested maps and lists become nested YAML:

```kotlin
extra.put("my-tool", mapOf("channels" to listOf("release", "beta"), "notify" to true))
```

```yaml
my-tool:
  channels:
    - release
    - beta
  notify: true
```

A key in `extra` may not be one of the YAML keys in the [Properties](#properties) table, such as `name`, `softdepend` or `commands`. This holds whether you set that property or not, so `extra` never overrides or adds to a value Plugin YML manages. Use the property instead. A clashing key stops the build, see [Validation](./validation.md#additional-values).

## Checks

Before writing the file, the plugin checks the name, the main class, the api version, command names and `provides`. The [Validation](./validation.md#bukkit) page lists every error.
