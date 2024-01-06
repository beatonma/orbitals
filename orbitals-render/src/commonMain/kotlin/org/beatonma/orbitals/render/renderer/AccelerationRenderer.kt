package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.Acceleration
import org.beatonma.orbitals.core.physics.AccelerationScalar
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitals.render.options.VisualOptions
import kotlin.math.log10
import kotlin.math.max


class AccelerationRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val scale = 2e1F

    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        val scaledAcceleration = Acceleration(
            AccelerationScalar(log10(body.acceleration.magnitude.value) * scale),
            body.acceleration.angle
        )

        delegate.drawLine(
            canvas,
            color = props.color.withOpacity(.5f),
            start = body.position,
            end = Position(
                scaledAcceleration.x.value,
                scaledAcceleration.y.value,
            ) + body.position,
            strokeWidth = max(1f, body.radius.value / 2),
        )
    }
}
