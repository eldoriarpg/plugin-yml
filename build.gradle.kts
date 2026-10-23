plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.spotless)
}

description = "A Gradle plugin that generates plugin.yml for Bukkit/Paper/BungeeCord/Nukkit plugins based on the Gradle project"

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.jackson.kotlin) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.jackson.yaml)

    testImplementation(kotlin("test"))
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

gradlePlugin {
    website = "https://docs.eldoria.de/plugin-yml"
    vcsUrl = "https://github.com/eldoriarpg/plugin-yml"

    plugins {
        register("bukkit") {
            id = "de.eldoria.plugin-yml.bukkit"
            displayName = "plugin-yml (Bukkit)"
            description = "Generate plugin.yml for Bukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bukkit.BukkitPlugin"
            tags = listOf("bukkit")
        }
        register("bungee") {
            id = "de.eldoria.plugin-yml.bungee"
            displayName = "plugin-yml (BungeeCord)"
            description = "Generate bungee.yml for BungeeCord plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bungee.BungeePlugin"
            tags = listOf("bungee")
        }
        register("nukkit") {
            id = "de.eldoria.plugin-yml.nukkit"
            displayName = "plugin-yml (Nukkit)"
            description = "Generate nukkit.yml for Nukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.nukkit.NukkitPlugin"
            tags = listOf("nukkit")
        }
        register("paper") {
            id = "de.eldoria.plugin-yml.paper"
            displayName = "plugin-yml (Paper)"
            description = "Generate paper-plugin.yml for Paper plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.paper.PaperPlugin"
            tags = listOf("paper")
        }
    }
}
