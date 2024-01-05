package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.VisualOptions
import kotlin.math.pow
import kotlin.math.sqrt


private const val EnterAnimationMillis = 450

class SimpleRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        if (body is GreatAttractor) {
            drawAttractor(canvas, body)
            return
        }

        val ageMillis = body.age.inWholeMilliseconds

        val radiusMultiplier = options.bodyScale * easeRadius(
            if (ageMillis > EnterAnimationMillis) 1f
            else {
                ageMillis.toFloat() / EnterAnimationMillis.toFloat()
            }
        )
        if (radiusMultiplier == 0f) return

        delegate.drawCircle(
            canvas,
            body.position,
            body.radius * radiusMultiplier,
            props.color,
            strokeWidth = options.strokeWidth,
            style = options.drawStyle,
            alpha = options.colorOptions.foregroundAlpha,
        )
    }

    private fun drawAttractor(canvas: Canvas, body: GreatAttractor) {
        delegate.drawCircle(
            canvas,
            body.position,
            maxOf(body.radius, 1.metres),
            0xffffff,
            strokeWidth = 8f,
            style = DrawStyle.Wireframe
        )
    }

    private fun easeRadius(value: Float): Float {
        return if (value < 0.5f) {
            (1f - sqrt(1f - (2f * value).pow(2f))) / 2f
        } else {
            (sqrt(1 - (-2f * value + 2).pow(2f)) + 1) / 2
        }
    }
}
