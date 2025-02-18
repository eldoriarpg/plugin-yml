/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml


fun assertApiVersion(apiVersion: String, valid: Regex, minApiVersion: Int) {
    if (!valid.matches(apiVersion)) throw InvalidPluginDescriptionException("Invalid api version: should match ${valid.pattern}")
    val splitVersion = apiVersion.split('.').map { v -> v.toInt() }
    if (splitVersion.size == 2) {
        if (splitVersion[1] < minApiVersion) throw InvalidPluginDescriptionException("Invalid api version: should be at least 1.${minApiVersion}")
    } else if (splitVersion.size == 3) {
        if (splitVersion[1] < 20) throw InvalidPluginDescriptionException("Invalid api version: Minor versions are not supported before 1.20.5")
        if (splitVersion[1] == 20 && splitVersion[2] < 5) throw InvalidPluginDescriptionException("Invalid api version: Minor versions are not supported before 1.20.5")
    } else {
        throw InvalidPluginDescriptionException("Invalid api version: ${valid.pattern}")
    }
}

fun assertNamespace(namespace: String?, name: String, invalid: Collection<String>) {
    for (invalidNamespace in invalid) {
        if (namespace?.startsWith(invalidNamespace) == true) {
            throw InvalidPluginDescriptionException("$name may not be within the $invalidNamespace namespace")
        }
    }
}
