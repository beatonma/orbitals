package org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas

import android.graphics.Canvas
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.OrbitalsRenderer

fun chooseRenderers(options: Options): List<OrbitalsRenderer<Canvas>> {
    return listOfNotNull(
        chooseRenderer(options.visualOptions.showTraceLines) {
            TrailRenderer(options.visualOptions.traceLineLength)
        },
        SimpleRenderer(options.visualOptions),
    )
}

private fun chooseRenderer(condition: Boolean, block: () -> OrbitalsRenderer<Canvas>): OrbitalsRenderer<Canvas>? {
    return if (condition) block() else null
}
