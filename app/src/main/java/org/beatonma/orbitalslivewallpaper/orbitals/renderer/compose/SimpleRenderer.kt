package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.BaseSimpleRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util.drawCircle
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util.toComposeColor

class SimpleRenderer(
    options: VisualOptions,
) : BaseSimpleRenderer<DrawScope, Color>(options) {
    override fun drawBody(canvas: DrawScope, body: Body) {
        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")
        canvas.drawCircle(
            body,
            color = color,
        )
    }

    override fun chooseColor(body: Body): Color =
        options.colorOptions.bodies.random().colors().random().toComposeColor()
}
