package org.beatonma.orbitalslivewallpaper.orbitals.render.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.render.CanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.render.util.toOffset


class AccelerationRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val Scale = 1e2F

    override fun drawBody(canvas: Canvas, body: Body) {
        delegate.drawLine(
            canvas,
            color = Color.Green.toArgb(),
            start = body.position.toOffset(),
            end = Offset(
                body.acceleration.x.value * Scale,
                body.acceleration.y.value * Scale,
            ) + body.position.toOffset(),
            alpha = 1f,
            strokeWidth = options.strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
