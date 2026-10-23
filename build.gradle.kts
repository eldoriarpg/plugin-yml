import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.publishdata)
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
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.add("-Xjdk-release=21")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

publishData {
    eldoNexus(dev = false)
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

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            url = "https://github.com/eldoriarpg/plugin-yml"
            licenses {
                license {
                    name = "MIT"
                    url = "https://opensource.org/license/mit"
                }
            }
            developers {
                developer {
                    id = "eldoriarpg"
                    name = "Eldoria"
                    url = "https://github.com/eldoriarpg"
                }
                developer {
                    id = "Minecrell"
                    name = "Minecrell"
                    url = "https://github.com/Minecrell"
                }
            }
            scm {
                url = "https://github.com/eldoriarpg/plugin-yml"
                connection = "scm:git:git://github.com/eldoriarpg/plugin-yml.git"
                developerConnection = "scm:git:ssh://github.com/eldoriarpg/plugin-yml.git"
            }
        }
    }
}
