@file:JvmName("Logging")

package org.beatonma.orbitals.core.util

import kotlin.jvm.JvmName

expect fun debug(content: Any?)
expect fun info(content: Any?)
expect fun warn(content: Any?)

fun <T> T.dump(label: String? = null): T = also {
    debug(
        when (label) {
            null -> it
            else -> "$label: $it"
        }
    )
}
