@file:JvmName("Performance")
package org.beatonma.orbitals.core.util

import kotlin.jvm.JvmName

expect fun currentTimeMillis(): Long

inline fun timeIt(
    maxMillis: Int = 15,
    label: String = "action",
    enabled: Boolean = false,
    warn: Boolean = false,
    block: () -> Unit
): Int {
    return if (!enabled) {
        block()
        0
    } else {
        val start = currentTimeMillis()

        block()

        val end = currentTimeMillis()
        val duration = end - start
        if (warn && duration > maxMillis) {
            warn("$label took ${duration}ms!")
        }
        duration.toInt()
    }
}
