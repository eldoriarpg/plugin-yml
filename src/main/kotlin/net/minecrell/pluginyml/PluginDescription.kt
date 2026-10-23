/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package net.minecrell.pluginyml

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input

/**
 * Settings shared by the description of every platform.
 */
abstract class PluginDescription(
    objects: ObjectFactory,
) {
    @Input @JsonIgnore
    var generateLibrariesJson: Boolean = false

    /**
     * Additional values written as top-level keys of the generated file, for keys plugin-yml does not model itself.
     *
     * Values may be strings, numbers, booleans, lists and nested maps. A key may not be one that plugin-yml writes
     * for the platform, whether that value is set or not; the build fails when it is.
     */
    @get:Input @get:JsonIgnore
    val extra: MapProperty<String, Any> = objects.mapProperty(String::class.java, Any::class.java)

    /**
     * Replaces the [extra] values. Allows `extra = [...]` in the Groovy DSL.
     */
    fun setExtra(values: Map<String, Any>) = extra.set(values)

    /**
     * The [extra] values as they are written to the generated file.
     */
    @JsonAnyGetter
    fun extraValues(): Map<String, Any> = extra.get()
}
