/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.artifacts.repositories.UrlArtifactRepository
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class GeneratePluginDescription : DefaultTask() {

    private val centralUrls: List<String> = listOf(
        "https://repo1.maven.org/maven2",
        "http://repo1.maven.org/maven2",
        "https://repo.maven.apache.org/maven2",
        "http://repo.maven.apache.org/maven2"
    )

    @Internal
    val eldonexusUrl: String = "https://eldonexus.de/repository/maven-public/"
    @Internal
    val googleUrl: String = "https://maven-central.storage-download.googleapis.com/maven2"

    /**
     * The filename for the generated plugin description file.
     */
    @get:Input
    abstract val fileName: Property<String>

    /**
     * The URL of the maven central proxy to use.
     * You can also use [useDefaultCentralProxy] and [useGoogleMavenCentralProxy] to set it to preconfigured values.
     */
    @get:Input
    @get:Optional
    abstract val mavenCentralProxies: MapProperty<String, String>

    /**
     * The filename for the generated libraries.json file.
     */
    @get:Input
    abstract val librariesJsonFileName: Property<String>

    @get:Input
    abstract val repos: MapProperty<String, String>

    @get:Input
    @get:Optional
    abstract val librariesRootComponent: Property<ResolvedComponentResult>

    @get:Nested
    abstract val pluginDescription: Property<PluginDescription>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val factory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)

        val module = SimpleModule()
        @Suppress("UNCHECKED_CAST") // Too stupid to figure out the generics here...
        module.addSerializer(
            StdDelegatingSerializer(
                NamedDomainObjectCollection::class.java,
                NamedDomainObjectCollectionConverter as Converter<NamedDomainObjectCollection<*>, *>
            )
        )
        module.addSerializer(StdDelegatingSerializer(UrlArtifactRepository::class.java, UrlArtifactRepositoryConverter))

        val mapper = ObjectMapper(factory)
            .registerKotlinModule()
            .registerModule(module)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val pluginDescription = pluginDescription.get()
        mapper.writeValue(outputDirectory.file(fileName).get().asFile, pluginDescription)

        if (pluginDescription.generateLibrariesJson) {
            val dependencies = librariesRootComponent.orNull.collectLibraries()
            val pluginLibraries = PluginLibraries(getRepositories(), dependencies)

            val jsonMapper = ObjectMapper()
                .registerKotlinModule()
                .registerModule(module)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            jsonMapper.writeValue(outputDirectory.file(librariesJsonFileName).get().asFile, pluginLibraries)
        }
    }

    private fun getRepositories(): Map<String, String> {
        val repositories = repos.get()
        val proxies = mavenCentralProxies.orNull?.takeIf { it.isNotEmpty() }
        var adjustedRepositories = repositories
        if (proxies != null) {
            adjustedRepositories = repositories.filter { (_, url) ->
                if (centralUrls.any { url.startsWith(it) }) {
                    logger.info("Removing mavenCentral url '$url'")
                    false
                } else true
            }.toMutableMap();
            logger.info("Using mavenCentral urls: ${proxies.values}")
            proxies.forEach { (name, url) -> adjustedRepositories[name] = url }
        }

        if (proxies == null && adjustedRepositories.values.any { repo -> centralUrls.any{c -> repo.startsWith(c)}}) {
            logger.warn("No mavenCentralProxy configured; using maven central directly is not encouraged.")
            logger.warn("Use useEldoNexusMavenCentralProxy() or useGoogleMavenCentralProxy() or set mavenCentralProxy in the generatePluginDescription task")
        }
        return adjustedRepositories
    }

    /**
     * Use the Google cache and our EldoNexus proxy for Maven Central as a backup.
     * Consider donating if you use our proxy: https://ko-fi.com/eldoriaplugins
     */
    fun useDefaultCentralProxy() {
        useGoogleMavenCentralProxy()
        useEldoNexusMavenCentralProxy()
    }

    fun useEldoNexusMavenCentralProxy() {
        addMavenCentralProxy("eldonexus_central_proxy", eldonexusUrl)
        logger.info("Registering EldoNexus Maven Central Proxy: $eldonexusUrl")
        logger.info("Consider donating if you use our proxy: https://ko-fi.com/eldoriaplugins")
    }

    private fun addMavenCentralProxy(name: String, url: String) {
        var proxies = mutableMapOf<String, String>()
        if (mavenCentralProxies.isPresent) {
            proxies = mavenCentralProxies.get().toMutableMap()
        }
        proxies[name] = url
        mavenCentralProxies.set(proxies)
    }

    /**
     * Use the Google proxy for Maven Central.
     * This proxy does only cache the most popular artifacts.
     * Artifacts of unpopular libraries may be missing.
     */
    fun useGoogleMavenCentralProxy() {
        addMavenCentralProxy("google_central_proxy", googleUrl)
        logger.info("Registering Google Maven Central Proxy: $googleUrl")
    }

    object NamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, Map<String, Any>>() {
        override fun convert(value: NamedDomainObjectCollection<Any>): Map<String, Any> {
            val namer = value.namer
            return value.associateBy { namer.determineName(it) }
        }
    }

    object UrlArtifactRepositoryConverter : StdConverter<UrlArtifactRepository, String>() {
        override fun convert(value: UrlArtifactRepository): String = value.url.toString()
    }

    data class PluginLibraries(
        val repositories: Map<String, String>,
        val dependencies: List<String>
    )

}
