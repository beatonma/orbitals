package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose

import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.OrbitalsRenderer

fun chooseRenderers(options: Options): List<OrbitalsRenderer<DrawScope>> {
    return listOfNotNull(
        chooseRenderer(options.visualOptions.showTraceLines) {
            TrailRenderer(options.visualOptions.traceLineLength)
        },
        SimpleRenderer(options.visualOptions),
    )
}

private fun chooseRenderer(condition: Boolean, block: () -> OrbitalsRenderer<DrawScope>): OrbitalsRenderer<DrawScope>? {
    return if (condition) block() else null
}
