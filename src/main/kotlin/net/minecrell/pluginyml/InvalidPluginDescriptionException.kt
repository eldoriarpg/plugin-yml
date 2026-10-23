/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package net.minecrell.pluginyml

class InvalidPluginDescriptionException(
    message: String,
) : Exception("Invalid plugin description: $message")
