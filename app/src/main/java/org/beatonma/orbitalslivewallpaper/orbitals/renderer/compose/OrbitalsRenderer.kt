package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.OrbitalsRenderer

class ComposeOrbitalsRenderer(
    val options: VisualOptions,
) : OrbitalsRenderer<DrawScope> {
    private val colors = mutableMapOf<UniqueID, Color>()

    override fun onBodyCreated(body: Body) {
        super.onBodyCreated(body)
        colors[body.id] = options.colorOptions.bodies.random().colors().random().toComposeColor()
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        colors.remove(body.id)
    }

    override fun drawBody(canvas: DrawScope, body: Body) {
        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")
        canvas.drawCircle(
            body,
            color = color,
        )
    }
}
