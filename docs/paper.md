---
sidebar_position: 3
title: Paper
---

# Paper plugins (paper-plugin.yml)

Use `de.eldoria.plugin-yml.paper` for a Paper plugin. It writes `paper-plugin.yml`, with Paper's own features: a bootstrapper, a loader, and separate dependency lists for bootstrap and server time.

For a classic `plugin.yml` that also runs on Bukkit and Spigot, use the [Bukkit](./bukkit.md) plugin instead.

## Full example

Kotlin DSL:

```kotlin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
}

version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperLibrary("com.google.code.gson:gson:2.10.1")
}

paper {
    main = "com.example.testplugin.TestPlugin"
    apiVersion = "1.21"

    bootstrapper = "com.example.testplugin.TestPluginBootstrap"
    loader = "com.example.testplugin.TestPluginLoader"
    hasOpenClassloader = false
    generateLibrariesJson = true
    foliaSupported = true

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Notch", "Jeb")
    prefix = "TEST"
    defaultPermission = BukkitPluginDescription.Permission.Default.OP
    provides = listOf("TestPluginOldName")

    bootstrapDependencies {
        register("WorldEdit")
        register("BeforePlugin") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    serverDependencies {
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Essentials") {
            required = false
            joinClasspath = false
        }
    }

    permissions {
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
    id 'de.eldoria.plugin-yml.paper' version '0.9.0'
}

version = '1.0.0'

repositories {
    mavenCentral()
    maven { url = 'https://repo.papermc.io/repository/maven-public/' }
}

dependencies {
    paperLibrary 'com.google.code.gson:gson:2.10.1'
}

paper {
    main = 'com.example.testplugin.TestPlugin'
    apiVersion = '1.21'

    bootstrapper = 'com.example.testplugin.TestPluginBootstrap'
    loader = 'com.example.testplugin.TestPluginLoader'
    hasOpenClassloader = false
    generateLibrariesJson = true
    foliaSupported = true

    load = 'STARTUP'
    authors = ['Notch', 'Jeb']
    prefix = 'TEST'
    defaultPermission = 'OP'
    provides = ['TestPluginOldName']

    bootstrapDependencies {
        'WorldEdit' {}
        'BeforePlugin' {
            required = false
            load = 'BEFORE'
        }
    }

    serverDependencies {
        'LuckPerms' {
            load = 'BEFORE'
        }
        'Essentials' {
            required = false
            joinClasspath = false
        }
    }

    permissions {
        'testplugin.test' {
            description = 'Allows you to run the test command'
            setDefault('OP')
        }
    }
}
```

The Paper block reuses two types from the Bukkit plugin: `BukkitPluginDescription.PluginLoadOrder` and `BukkitPluginDescription.Permission`. That is why the Kotlin example imports both classes.

## Properties

Every property is optional unless the table says otherwise.

| Property | Type | Default | YAML key |
|---|---|---|---|
| `name` | `String` | project name | `name` |
| `version` | `String` | project version | `version` |
| `main` | `String` | none, **required** | `main` |
| `apiVersion` | `String` | none, **required** | `api-version` |
| `bootstrapper` | `String` | none | `bootstrapper` |
| `loader` | `String` | none | `loader` |
| `description` | `String` | project description | `description` |
| `load` | `PluginLoadOrder` | none | `load` |
| `author` | `String` | `author` project property | `author` |
| `authors` | `List<String>` | none | `authors` |
| `contributors` | `List<String>` | none | `contributors` |
| `website` | `String` | `url` project property | `website` |
| `prefix` | `String` | none | `prefix` |
| `defaultPermission` | `Permission.Default` | none | `defaultPerm` |
| `provides` | `List<String>` | none | `provides` |
| `hasOpenClassloader` | `Boolean` | none | `has-open-classloader` |
| `foliaSupported` | `Boolean` | none | `folia-supported` |
| `generateLibrariesJson` | `Boolean` | `false` | not written, see [Libraries](./libraries.md) |
| `extra` | `Map<String, Any>` | empty | top-level keys, see [Additional values](#additional-values) |
| `bootstrapDependencies` | container of dependencies | empty | `dependencies.bootstrap` |
| `serverDependencies` | container of dependencies | empty | `dependencies.server` |
| `permissions` | container of permissions | empty | `permissions` |

`load` takes `STARTUP` or `POSTWORLD`. `Permission.Default` takes `TRUE`, `FALSE`, `OP` or `NOT_OP`, written as `true`, `false`, `op` and `'!op'`.

Permissions work exactly like on Bukkit, including `children` and `childrenMap`. See [Permissions](./bukkit.md#permissions).

The Paper block has no `commands`, `depend`, `softDepend`, `loadBefore` or `libraries`. Dependencies go into the two dependency containers below. Libraries go through a loader, see [Libraries](./libraries.md).

### API version

`apiVersion` is required for Paper and has to be at least `1.19`. Patch versions such as `1.21.4` are accepted from `1.20.5` on, and the new version numbers such as `26.1` work too. The full rules are on the [Validation](./validation.md#api-version-rules) page.

### Bootstrapper and loader

`bootstrapper` and `loader` take the fully qualified class names of your bootstrap and loader classes. The loader is the place where a Paper plugin adds its libraries at runtime. [Libraries](./libraries.md#paper-loader) has a complete loader that reads the file Plugin YML generates.

## Dependencies

Paper splits dependencies into two lists:

- `bootstrapDependencies` apply while plugins bootstrap.
- `serverDependencies` apply while the server runs.

Register each dependency by the other plugin's name. Every dependency has three settings:

| Property | Type | Default | YAML key | Meaning |
|---|---|---|---|---|
| `load` | `RelativeLoadOrder` | `OMIT` | `load` | `BEFORE` loads the other plugin before yours, `AFTER` after yours, `OMIT` sets no order. |
| `required` | `Boolean` | `true` | `required` | Whether your plugin needs the other plugin to be present. |
| `joinClasspath` | `Boolean` | `true` | `join-classpath` | Whether your plugin can use the other plugin's classes. |

`RelativeLoadOrder` is `PaperPluginDescription.RelativeLoadOrder`. All three settings are always written, so the defaults show up in the file too.

The example above produces this section:

```yaml
dependencies:
  server:
    Essentials:
      load: OMIT
      required: false
      join-classpath: false
    LuckPerms:
      load: BEFORE
      required: true
      join-classpath: true
  bootstrap:
    BeforePlugin:
      load: BEFORE
      required: false
      join-classpath: true
    WorldEdit:
      load: OMIT
      required: true
      join-classpath: true
```

When both lists are empty, no `dependencies` section is written.

To use classes of another plugin in your code, add that plugin's API as a `compileOnly` dependency as usual.

## Additional values

Keys that Plugin YML does not know go into `extra`. Every entry is written as a top-level key of `paper-plugin.yml`, and values can be strings, numbers, booleans, lists and nested maps:

```kotlin
paper {
    extra.put("my-tool", mapOf("channels" to listOf("release", "beta")))
}
```

```groovy
paper {
    extra = ['my-tool': [channels: ['release', 'beta']]]
}
```

A key may not be one of the YAML keys in the [Properties](#properties) table, including `dependencies`, whether that property is set or not. [Additional values](./bukkit.md#additional-values) on the Bukkit page has the details.

## Checks

Before writing the file, the plugin checks the name, the version, the api version, the main, bootstrapper and loader classes, dependency names and `provides`. The [Validation](./validation.md#paper) page lists every error.
