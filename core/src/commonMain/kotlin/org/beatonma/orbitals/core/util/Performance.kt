@file:JvmName("Performance")

package org.beatonma.orbitals.core.util

import org.beatonma.orbitals.core.OrbitalsBuildConfig
import kotlin.jvm.JvmName

expect fun currentTimeMillis(): Long

inline fun timeIt(
    label: String = "action",
    enabled: Boolean = OrbitalsBuildConfig.DEBUG,
    debug: Boolean = false,
    warn: Boolean = false,
    warnMillis: Int = 15,
    block: () -> Unit,
): Int {
    return if (!enabled) {
        block()
        0
    } else {
        val start = currentTimeMillis()

        block()

        val end = currentTimeMillis()
        val duration = end - start
        if (debug) {
            debug("$label took ${duration}ms")
        }
        if (warn && duration > warnMillis) {
            warn("$label took ${duration}ms!")
        }
        duration.toInt()
    }
}
