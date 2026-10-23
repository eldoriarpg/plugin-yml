/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package net.minecrell.pluginyml

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ExtraValuesTest {
    @TempDir
    lateinit var projectDir: File

    private fun kotlinBuild(
        platform: String,
        block: String,
    ) {
        projectDir.resolve("settings.gradle.kts").writeText("rootProject.name = \"TestPlugin\"\n")
        projectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                java
                id("de.eldoria.plugin-yml.$platform")
            }

            version = "1.0.0"

            $platform {
            $block
            }
            """.trimIndent(),
        )
    }

    private fun groovyBuild(
        platform: String,
        block: String,
    ) {
        projectDir.resolve("settings.gradle").writeText("rootProject.name = 'TestPlugin'\n")
        projectDir.resolve("build.gradle").writeText(
            """
            plugins {
                id 'java'
                id 'de.eldoria.plugin-yml.$platform'
            }

            version = '1.0.0'

            $platform {
            $block
            }
            """.trimIndent(),
        )
    }

    private fun runner(vararg arguments: String): GradleRunner =
        GradleRunner
            .create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(*arguments, "--configuration-cache", "--stacktrace")

    private fun generate(platform: String): BuildResult = runner(task(platform)).build()

    private fun task(platform: String) = "generate${platform.replaceFirstChar(Char::uppercase)}PluginDescription"

    private fun output(
        platform: String,
        fileName: String,
    ): String = projectDir.resolve("build/generated/plugin-yml/${platform.replaceFirstChar(Char::uppercase)}/$fileName").readText()

    @Test
    fun `kotlin dsl writes nested extra values to plugin yml`() {
        kotlinBuild(
            "bukkit",
            """
            main = "com.example.TestPlugin"
            extra.put("modrinth-project-id", "djC8I9ui")
            extra.put("spigot-resource-id", 107545)
            extra.put("pluginupdater", mapOf("source" to "modrinth", "channels" to listOf("release", "beta")))
            """,
        )

        generate("bukkit")

        assertEquals(
            """
            name: TestPlugin
            version: 1.0.0
            main: com.example.TestPlugin
            modrinth-project-id: djC8I9ui
            spigot-resource-id: 107545
            pluginupdater:
              source: modrinth
              channels:
                - release
                - beta

            """.trimIndent(),
            output("bukkit", "plugin.yml"),
        )
    }

    @Test
    fun `kotlin dsl assignment replaces extra values`() {
        kotlinBuild(
            "paper",
            """
            main = "com.example.TestPlugin"
            apiVersion = "1.21"
            extra.put("dropped", true)
            extra = mapOf("pluginupdater" to mapOf("source" to "modrinth"))
            """,
        )

        generate("paper")

        assertEquals(
            """
            api-version: "1.21"
            name: TestPlugin
            version: 1.0.0
            main: com.example.TestPlugin
            pluginupdater:
              source: modrinth

            """.trimIndent(),
            output("paper", "paper-plugin.yml"),
        )
    }

    @Test
    fun `groovy dsl writes extra values`() {
        groovyBuild(
            "bukkit",
            """
            main = 'com.example.TestPlugin'
            extra = [pluginupdater: [source: 'modrinth', channels: ['release']]]
            extra.put('github-repo', 'eldoriarpg/plugin-yml')
            """,
        )

        generate("bukkit")

        assertEquals(
            """
            name: TestPlugin
            version: 1.0.0
            main: com.example.TestPlugin
            pluginupdater:
              source: modrinth
              channels:
                - release
            github-repo: eldoriarpg/plugin-yml

            """.trimIndent(),
            output("bukkit", "plugin.yml"),
        )
    }

    @Test
    fun `bungee and nukkit write extra values`() {
        kotlinBuild(
            "bungee",
            """
            main = "com.example.TestPlugin"
            extra.put("custom", mapOf("key" to "value"))
            """,
        )
        generate("bungee")
        assertContains(output("bungee", "bungee.yml"), "custom:\n  key: value\n")

        kotlinBuild(
            "nukkit",
            """
            main = "com.example.TestPlugin"
            api = listOf("1.0.0")
            extra.put("custom", "value")
            """,
        )
        generate("nukkit")
        assertContains(output("nukkit", "nukkit.yml"), "custom: value\n")
    }

    @Test
    fun `extra key clashing with a modelled field fails the build`() {
        kotlinBuild(
            "bukkit",
            """
            main = "com.example.TestPlugin"
            extra.put("name", "Other")
            extra.put("softdepend", listOf("Essentials"))
            extra.put("allowed", true)
            """,
        )

        val result = runner(task("bukkit")).buildAndFail()

        assertContains(
            result.output,
            "Invalid plugin description: Extra values may not use keys plugin-yml writes itself: 'name', 'softdepend'.",
        )
    }

    @Test
    fun `paper clash check uses the written key names`() {
        kotlinBuild(
            "paper",
            """
            main = "com.example.TestPlugin"
            apiVersion = "1.21"
            extra.put("has-open-classloader", true)
            extra.put("dependencies", mapOf("server" to emptyMap<String, Any>()))
            """,
        )

        val result = runner(task("paper")).buildAndFail()

        assertContains(result.output, "'has-open-classloader', 'dependencies'")
    }

    @Test
    fun `changing extra values reruns the task and the configuration cache is reused`() {
        val block = { value: String ->
            """
            main = "com.example.TestPlugin"
            extra.put("custom", "$value")
            """
        }
        kotlinBuild("bukkit", block("one"))
        assertEquals(TaskOutcome.SUCCESS, generate("bukkit").task(":generateBukkitPluginDescription")?.outcome)

        val second = generate("bukkit")
        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":generateBukkitPluginDescription")?.outcome)
        assertContains(second.output, "Reusing configuration cache.")

        kotlinBuild("bukkit", block("two"))
        assertEquals(TaskOutcome.SUCCESS, generate("bukkit").task(":generateBukkitPluginDescription")?.outcome)
        assertContains(output("bukkit", "plugin.yml"), "custom: two\n")
    }
}
