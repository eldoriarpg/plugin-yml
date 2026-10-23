---
sidebar_position: 5
title: Nukkit
---

# Nukkit (nukkit.yml)

Use `de.eldoria.plugin-yml.nukkit` for a Nukkit plugin. It writes `nukkit.yml`.

## Full example

Kotlin DSL:

```kotlin
import net.minecrell.pluginyml.nukkit.NukkitPluginDescription

plugins {
    java
    id("de.eldoria.plugin-yml.nukkit") version "0.9.0"
}

version = "1.0.0"

nukkit {
    main = "com.example.testplugin.TestPlugin"
    api = listOf("1.0.0")

    load = NukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Notch", "Jeb")
    depend = listOf("PlotSquared")
    softDepend = listOf("LuckPerms")
    loadBefore = listOf("BrokenPlugin")
    prefix = "TEST"

    commands {
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "/test"
        }
    }

    permissions {
        register("testplugin.*") {
            description = "Allows you to run all testplugin commands"
            children {
                register("testplugin.test") {
                    description = "Allows you to run the test command"
                    default = NukkitPluginDescription.Permission.Default.OP
                }
            }
        }
    }
}
```

Groovy DSL:

```groovy
plugins {
    id 'java'
    id 'de.eldoria.plugin-yml.nukkit' version '0.9.0'
}

version = '1.0.0'

nukkit {
    main = 'com.example.testplugin.TestPlugin'
    api = ['1.0.0']

    load = 'STARTUP'
    authors = ['Notch', 'Jeb']
    depend = ['PlotSquared']
    softDepend = ['LuckPerms']
    loadBefore = ['BrokenPlugin']
    prefix = 'TEST'

    commands {
        test {
            description = 'This is a test command!'
            aliases = ['t']
            permission = 'testplugin.test'
            usage = '/test'
        }
    }

    permissions {
        'testplugin.*' {
            description = 'Allows you to run all testplugin commands'
            children {
                'testplugin.test' {
                    description = 'Allows you to run the test command'
                    setDefault('OP')
                }
            }
        }
    }
}
```

The permissions part produces:

```yaml
permissions:
  testplugin.*:
    description: Allows you to run all testplugin commands
    children:
      testplugin.test:
        description: Allows you to run the test command
        default: op
```

## Properties

Every property is optional unless the table says otherwise.

| Property | Type | Default | YAML key |
|---|---|---|---|
| `name` | `String` | project name | `name` |
| `main` | `String` | none, **required** | `main` |
| `version` | `String` | project version | `version` |
| `api` | `List<String>` | none, **required** | `api` |
| `description` | `String` | project description | `description` |
| `load` | `PluginLoadOrder` | none | `load` |
| `author` | `String` | `author` project property | `author` |
| `authors` | `List<String>` | none | `authors` |
| `website` | `String` | `url` project property | `website` |
| `depend` | `List<String>` | none | `depend` |
| `softDepend` | `List<String>` | none | `softdepend` |
| `loadBefore` | `List<String>` | none | `loadbefore` |
| `prefix` | `String` | none | `prefix` |
| `generateLibrariesJson` | `Boolean` | `false` | not written, see [Libraries](./libraries.md) |
| `commands` | container of commands | empty | `commands` |
| `permissions` | container of permissions | empty | `permissions` |

`PluginLoadOrder` is `NukkitPluginDescription.PluginLoadOrder` with the values `STARTUP` and `POSTWORLD`.

## Commands

Commands work like on Bukkit:

| Property | Type | YAML key |
|---|---|---|
| `description` | `String` | `description` |
| `aliases` | `List<String>` | `aliases` |
| `permission` | `String` | `permission` |
| `permissionMessage` | `String` | `permission-message` |
| `usage` | `String` | `usage` |

A command name and its aliases may not contain `:`.

## Permissions

Nukkit permissions can nest. Each permission has:

| Property | Type | YAML key |
|---|---|---|
| `description` | `String` | `description` |
| `default` | `Permission.Default` | `default` |
| `children` | container of permissions | `children` |

`children` is a container of full permissions, not a list of names. Register child permissions inside it the same way as top level ones, as shown in the example.

`Permission.Default` is `NukkitPluginDescription.Permission.Default`. `TRUE`, `FALSE`, `OP` and `NOT_OP` are written as `true`, `false`, `op` and `'!op'`.

## Libraries

Nukkit has no `libraries` entry in its description file. You can still declare `nukkitLibrary` dependencies and have them written to `nukkit-libraries.json`. Your plugin then has to load them itself. See [Libraries](./libraries.md).

## Checks

The plugin checks the name, version, main class, api list and command names. See [Validation](./validation.md#nukkit).
