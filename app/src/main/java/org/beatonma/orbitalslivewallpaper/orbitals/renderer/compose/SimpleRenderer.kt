package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.DrawStyle
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.BaseSimpleRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util.drawCircle
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util.toComposeColor

class SimpleRenderer(
    options: VisualOptions,
) : BaseSimpleRenderer<DrawScope, Color>(options) {
    private val style: androidx.compose.ui.graphics.drawscope.DrawStyle =
        if (options.drawStyle == DrawStyle.Wireframe) {
            Stroke()
        } else {
            Fill
        }

    override fun drawBody(canvas: DrawScope, body: Body) {
        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")
        canvas.drawCircle(
            body,
            color = color,
            style = style,
        )
    }

    override fun chooseColor(body: Body): Color =
        options.colorOptions.bodies.random().colors().random().toComposeColor()
}
