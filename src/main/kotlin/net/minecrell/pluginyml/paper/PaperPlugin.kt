/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.paper

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import net.minecrell.pluginyml.assertApiVersion
import net.minecrell.pluginyml.assertNamespace
import org.gradle.api.Project

class PaperPlugin : PlatformPlugin<PaperPluginDescription>("Paper", "paper-plugin.yml") {

    companion object {
        @JvmStatic
        private val VALID_NAME = Regex("^[A-Za-z0-9 _.-]+$")

        @JvmStatic
        private val VALID_API_VERSION = Regex("^1\\.[1-9][0-9]*(\\.[1-9][0-9]*)?$")

        @JvmStatic
        private val INVALID_NAMESPACES =
            listOf("net.minecraft.", "org.bukkit.", "io.papermc.paper.", "com.destroystokoyo.paper.")

    }

    override fun createExtension(project: Project) = PaperPluginDescription(project)

    override fun setDefaults(project: Project, description: PaperPluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.website = description.website ?: project.findProperty("url")?.toString()
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

    override fun validate(description: PaperPluginDescription) {
        val name = description.name ?: throw InvalidPluginDescriptionException("Plugin name is not set")
        if (!VALID_NAME.matches(name)) throw InvalidPluginDescriptionException("Invalid plugin name: should match $VALID_NAME")

        if (description.version.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin version is not set")
        description.apiVersion ?: throw InvalidPluginDescriptionException("Plugin API version is not set")
        description.apiVersion?.let { apiVersion ->
            assertApiVersion(apiVersion, VALID_API_VERSION, 19)
        }

        val main = description.main ?: throw InvalidPluginDescriptionException("Main class is not defined")
        if (main.isEmpty()) throw InvalidPluginDescriptionException("Main class cannot be empty")
        assertNamespace(description.main, "Main", INVALID_NAMESPACES)
        assertNamespace(description.bootstrapper, "Bootstrapper", INVALID_NAMESPACES)
        assertNamespace(description.loader, "Loader", INVALID_NAMESPACES)

        for (serverDependency in description.serverDependencies) {
            if (serverDependency.name.isEmpty()) throw InvalidPluginDescriptionException("Plugin name in serverDependencies can not be empty")
        }
        for (bootstrapDependency in description.bootstrapDependencies) {
            if (bootstrapDependency.name.isEmpty()) throw InvalidPluginDescriptionException("Plugin name in bootstrapDependencies can not be empty")
        }

        if (description.provides?.all(VALID_NAME::matches) == false) {
            throw InvalidPluginDescriptionException("Invalid plugin provides name: all should match $VALID_NAME")
        }
    }
}
