package org.beatonma.orbitalslivewallpaper.orbitals.renderer.simple

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.DrawStyle
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.drawCircle
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.toComposeColor

class ComposeSimpleRenderer(
    options: VisualOptions,
) : BaseSimpleRenderer<DrawScope, Color>(options) {
    private fun style(): androidx.compose.ui.graphics.drawscope.DrawStyle {
        return if (options.drawStyle == DrawStyle.Wireframe) {
            Stroke(options.strokeWidth)
        }
        else {
            Fill
        }
    }

    override fun drawBody(canvas: DrawScope, body: Body) {
        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")
        canvas.drawCircle(
            body,
            color = color,
            style = style(),
        )
    }

    override fun chooseColor(body: Body): Color = options.colorOptions.colorForBody.toComposeColor()
}
