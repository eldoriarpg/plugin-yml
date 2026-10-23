---
sidebar_position: 6
title: Libraries
---

# Libraries

Your plugin often needs libraries that the server does not ship. Instead of shading them into your jar, you can let the server download them when it loads your plugin. Plugin YML collects those libraries from your build script and writes them where the platform expects them.

## Declare libraries

Every platform plugin adds two dependency configurations:

| Configuration | Used by |
|---|---|
| `library` | every platform plugin applied in the project |
| `bukkitLibrary`, `paperLibrary`, `bungeeLibrary`, `nukkitLibrary` | only that platform |

Kotlin DSL:

```kotlin
dependencies {
    library("com.google.code.gson:gson:2.10.1")
    bukkitLibrary("org.apache.commons:commons-lang3:3.14.0")
}
```

Groovy DSL:

```groovy
dependencies {
    library 'com.google.code.gson:gson:2.10.1'
    bukkitLibrary 'org.apache.commons:commons-lang3:3.14.0'
}
```

These dependencies are added to `compileOnly`. You can use them in your code, and they are not packed into your jar.

## What gets written

Plugin YML resolves the library configuration of the platform and writes each library as `group:artifact:version`:

- Only the libraries you declared are listed. Their own dependencies are not.
- The version is the one Gradle picked after resolving, so version conflicts are already settled.
- Every library has to resolve. If Gradle cannot find one, the build fails with `No moduleVersion for ...`.

Where the list goes depends on the platform:

| Platform | Where the libraries go |
|---|---|
| Bukkit | the `libraries` key of `plugin.yml` |
| BungeeCord | the `libraries` key of `bungee.yml` |
| Paper | only `paper-libraries.json`, read by your loader |
| Nukkit | only `nukkit-libraries.json`, read by your own code |

On Bukkit and BungeeCord you can also set `libraries` by hand. Those entries stay first in the list and the resolved libraries are added after them.

## The libraries JSON file

Paper plugins and Nukkit plugins cannot list libraries in their description file. Your plugin has to load them itself. To make that easy, Plugin YML can write the libraries and your repositories into a JSON file. Turn it on in the platform block:

```kotlin
paper {
    generateLibrariesJson = true
}
```

```groovy
paper {
    generateLibrariesJson = true
}
```

The file sits next to the description file in the root of your jar:

| Platform | File |
|---|---|
| Bukkit | `bukkit-libraries.json` |
| Paper | `paper-libraries.json` |
| BungeeCord | `bungee-libraries.json` |
| Nukkit | `nukkit-libraries.json` |

For the [Paper example](./paper.md#full-example) with `useDefaultCentralProxy()` (see below) it looks like this:

```json
{
  "repositories": {
    "maven": "https://repo.papermc.io/repository/maven-public/",
    "google_central_proxy": "https://maven-central.storage-download.googleapis.com/maven2",
    "eldonexus_central_proxy": "https://eldonexus.de/repository/maven-public/"
  },
  "dependencies": [
    "com.google.code.gson:gson:2.10.1"
  ]
}
```

`repositories` maps the name of each Maven repository to its URL. `maven` is the name Gradle gives a repository added with `maven("...")` when you do not name it. `dependencies` is the same list described above. The real file is written on one line.

The option works on Bukkit and BungeeCord too, but you rarely need it there because the server already reads `libraries`. On Bukkit, entries you set by hand in `libraries` are not part of the JSON file.

:::warning
Only repositories declared in the `repositories {}` block of the project end up in the file. Repositories declared only in `settings.gradle(.kts)` under `dependencyResolutionManagement` are not included, and the `repositories` part of the file stays empty.
:::

## Maven Central proxies

Downloading libraries straight from Maven Central at server start is discouraged. Paper even logs a warning when a plugin does it. Instead, point the server at a mirror of Maven Central.

Plugin YML can swap Maven Central for one or more mirrors in the JSON file. Configure this on the generate task of your platform:

Kotlin DSL:

```kotlin
tasks {
    generatePaperPluginDescription {
        useDefaultCentralProxy()
    }
}
```

Groovy DSL:

```groovy
tasks.named('generatePaperPluginDescription') {
    useDefaultCentralProxy()
}
```

These methods are available on every generate task:

| Method | Adds |
|---|---|
| `useDefaultCentralProxy()` | the Google mirror, then the EldoNexus mirror as a backup |
| `useGoogleMavenCentralProxy()` | `google_central_proxy`: `https://maven-central.storage-download.googleapis.com/maven2` |
| `useEldoNexusMavenCentralProxy()` | `eldonexus_central_proxy`: `https://eldonexus.de/repository/maven-public/` |
| `addMavenCentralProxy(name, url)` | your own mirror under the given name |

You can call them more than once. As soon as at least one mirror is set:

1. Every Maven Central URL is removed from the repositories in the JSON file.
2. The mirrors are added after your other repositories, in the order you added them.

Keep that order when your loader adds the repositories. The Google mirror only caches popular artifacts, so the backup after it matters.

If Maven Central is in your repositories and no mirror is set, the build still succeeds but logs `No mavenCentralProxy configured; using maven central directly is not encouraged.`

Mirrors only change the JSON file. The `libraries` key of `plugin.yml` and `bungee.yml` holds no repositories.

## Paper loader

On Paper, a plugin loader adds the libraries to your plugin's classpath. Register your loader class and turn on the JSON file:

```kotlin
paper {
    main = "com.example.testplugin.TestPlugin"
    apiVersion = "1.21"
    loader = "com.example.testplugin.PluginLibrariesLoader"
    generateLibrariesJson = true
}

tasks {
    generatePaperPluginDescription {
        useDefaultCentralProxy()
    }
}
```

This loader reads `paper-libraries.json` and hands everything to Paper's `MavenLibraryResolver`:

```java
package com.example.testplugin;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PluginLibrariesLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load() {
        try (InputStream in = getClass().getResourceAsStream("/paper-libraries.json")) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            return repositories.entrySet().stream()
                    .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
        }
    }
}
```

## Nukkit

Plugin YML ships no loader for Nukkit. With `generateLibrariesJson = true` you get `nukkit-libraries.json` in the same format as above, and your plugin has to download and load the libraries on its own.
