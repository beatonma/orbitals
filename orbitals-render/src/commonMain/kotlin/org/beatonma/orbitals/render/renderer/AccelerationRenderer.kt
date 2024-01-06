package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer


class AccelerationRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val scale = 1e2F

    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        delegate.drawLine(
            canvas,
            color = props.color.withOpacity(.5f),
            start = body.position,
            end = Position(
                body.acceleration.x.value * scale,
                body.acceleration.y.value * scale,
            ) + body.position,
            alpha = 1f,
            strokeWidth = options.strokeWidth,
        )
    }
}
