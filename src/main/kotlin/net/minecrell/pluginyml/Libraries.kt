/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package net.minecrell.pluginyml

import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

fun ResolvedComponentResult?.collectLibraries(additional: List<String>? = null): List<String> {
    val resolved =
        this?.dependencies?.map { d ->
            (d as? ResolvedDependencyResult)?.selected?.moduleVersion?.toString() ?: error("No moduleVersion for $d")
        }
    return ((additional ?: listOf()) + (resolved ?: listOf())).distinct()
}
