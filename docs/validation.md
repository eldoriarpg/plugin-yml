---
sidebar_position: 7
title: Validation
---

# Validation

Plugin YML checks your settings before it writes the description file. When a check fails, the build stops and the file is not written. This page lists every check, so you can find the cause of an error quickly.

## How an error looks

Errors from Plugin YML start with `Invalid plugin description:`, for example:

```text
Execution failed for task ':generateBukkitPluginDescription' (registered by plugin 'de.eldoria.plugin-yml.bukkit').
> net.minecrell.pluginyml.InvalidPluginDescriptionException: Invalid plugin description: Main class cannot be empty
```

## Missing required values

When a required property is not set at all, Gradle stops the build before Plugin YML gets to run its own checks. The message then comes from Gradle:

```text
Value not set
  ... property 'pluginDescription.main' doesn't have a configured value
```

The property after `pluginDescription.` is the one to set:

| Property | Platforms |
|---|---|
| `main` | all |
| `apiVersion` | Paper |
| `api` | Nukkit |

## API version rules

Bukkit and Paper check `apiVersion` the same way. Only the lowest allowed version differs.

| Rule | Bukkit | Paper |
|---|---|---|
| Required | no | yes |
| Lowest `1.x` version | `1.13` | `1.19` |

The rules:

1. The version has two or three parts: `major.minor` or `major.minor.patch`.
2. The major part has one or two digits. The minor and patch parts start with a digit from 1 to 9, so `0` and leading zeros are not allowed.
3. The major part is `1`, or `26` and higher. The new version numbering, such as `26.1` and `26.1.1`, is accepted since Plugin YML 0.9.0.
4. A two part `1.x` version must be at least the lowest version in the table.
5. A three part `1.x.y` version must be at least `1.20.5`.
6. Versions from `26` on have no further limits. Since the major part has at most two digits, `99` is the highest.

| Version | Bukkit | Paper | Error |
|---|---|---|---|
| `1.13` | valid | invalid | `should be at least 1.19` |
| `1.19` | valid | valid | |
| `1.20.4` | invalid | invalid | `Minor versions are not supported before 1.20.5` |
| `1.20.5` | valid | valid | |
| `1.21` | valid | valid | |
| `1.21.11` | valid | valid | |
| `26.1` | valid | valid | |
| `26.1.1` | valid | valid | |
| `1.12` | invalid | invalid | `should be at least 1.13` (Bukkit) |
| `1.021` | invalid | invalid | `should match ...` |
| `26.0` | invalid | invalid | `should match ...` |
| `25.1` | invalid | invalid | `Invalid major version 25` |

The errors you can see:

| Message | Cause |
|---|---|
| `Invalid api version: should match ^\d{1,2}\.[1-9][0-9]*(\.[1-9][0-9]*)?$` | The version has the wrong shape, see rules 1 and 2. |
| `Invalid api version: Invalid major version <n>` | The major part is 0 or between 2 and 25. |
| `Invalid api version: should be at least 1.13` or `1.19` | A two part `1.x` version is too old. |
| `Invalid api version: Minor versions are not supported before 1.20.5` | A three part version below `1.20.5`. |

## Bukkit

| Message | Cause |
|---|---|
| `Invalid plugin name: should match ^[A-Za-z0-9 _.-]+$` | `name` has characters other than letters, digits, space, `_`, `.` and `-`. |
| `Plugin version is not set` | `version` is empty. |
| `Main class cannot be empty` | `main` is an empty string. |
| `Main may not be within the <prefix> namespace` | `main` starts with `net.minecraft.`, `org.bukkit.`, `io.papermc.` or `org.spigotmc`. |
| `Command '<name>' cannot contain ':'` | A command name contains `:`. |
| `Alias '<alias>' of '<name>' cannot contain ':'` | A command alias contains `:`. |
| `Invalid plugin provides name: all should match ^[A-Za-z0-9 _.-]+$` | An entry in `provides` breaks the name rule. |

Plus the [API version rules](#api-version-rules) when `apiVersion` is set.

## Paper

| Message | Cause |
|---|---|
| `Invalid plugin name: should match ^[A-Za-z0-9 _.-]+$` | `name` has characters other than letters, digits, space, `_`, `.` and `-`. |
| `Plugin version is not set` | `version` is empty. |
| `Main class cannot be empty` | `main` is an empty string. |
| `Main may not be within the <prefix> namespace` | `main` starts with `net.minecraft.`, `org.bukkit.` or `io.papermc.paper.`. |
| `Bootstrapper may not be within the <prefix> namespace` | Same rule for `bootstrapper`. |
| `Loader may not be within the <prefix> namespace` | Same rule for `loader`. |
| `Plugin name in serverDependencies can not be empty` | A server dependency was registered with an empty name. |
| `Plugin name in bootstrapDependencies can not be empty` | A bootstrap dependency was registered with an empty name. |
| `Invalid plugin provides name: all should match ^[A-Za-z0-9 _.-]+$` | An entry in `provides` breaks the name rule. |

Plus the [API version rules](#api-version-rules). On Paper they always apply, because `apiVersion` is required.

## BungeeCord

| Message | Cause |
|---|---|
| `Plugin name is not set` | `name` is empty. |
| `Main class is not defined` | `main` is empty. |

## Nukkit

| Message | Cause |
|---|---|
| `Plugin name is not set` | `name` is empty. |
| `Plugin version class is not set` | `version` is empty. |
| `Main class cannot be empty` | `main` is an empty string. |
| `Main class cannot be within cn.nukkit. package` | `main` starts with `cn.nukkit.`. |
| `Nukkit API version is not set` | `api` is an empty list. |
| `Command '<name>' cannot contain ':'` | A command name contains `:`. |
| `Alias '<alias>' of '<name>' cannot contain ':'` | A command alias contains `:`. |

## Library errors

| Message | Cause |
|---|---|
| `No moduleVersion for <dependency>` | A `library` or `<platform>Library` dependency could not be resolved. Check its coordinates and your repositories. |

See [Libraries](./libraries.md).
