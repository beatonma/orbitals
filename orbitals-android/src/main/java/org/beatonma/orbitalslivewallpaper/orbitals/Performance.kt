package org.beatonma.orbitalslivewallpaper.orbitals

import com.beatonma.orbitalslivewallpaper.BuildConfig
import org.beatonma.orbitalslivewallpaper.warn

inline fun timeIt(
    maxMillis: Int = 15,
    label: String = "action",
    enabled: Boolean = BuildConfig.DEBUG,
    block: () -> Unit
) {
    if (!enabled) {
        block()
        return
    } else {
        val start = System.currentTimeMillis()

        block()

        val end = System.currentTimeMillis()
        val duration = end - start
        if (duration > maxMillis) {
            warn("$label took ${duration}ms!")
        }
    }
}
