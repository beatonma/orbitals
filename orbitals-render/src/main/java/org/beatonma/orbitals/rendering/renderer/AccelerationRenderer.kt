package org.beatonma.orbitals.rendering.renderer

import org.beatonma.orbitals.options.CapStyle
import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.rendering.CanvasDelegate
import org.beatonma.orbitals.rendering.OrbitalsRenderer


class AccelerationRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val scale = 1e2F

    override fun drawBody(canvas: Canvas, body: Body) {
        delegate.drawLine(
            canvas,
            color = 0xff_00ff00.toInt(),
            start = body.position,
            end = Position(
                body.acceleration.x.value * scale,
                body.acceleration.y.value * scale,
            ) + body.position,
            alpha = 1f,
            strokeWidth = options.strokeWidth,
            cap = CapStyle.Round,
        )
    }
}
