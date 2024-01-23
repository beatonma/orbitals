package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.GreatAttractor
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
        val renderRadius = getRenderRadius(body) ?: return

        when (body) {
            is GreatAttractor -> {
                delegate.drawCircle(
                    canvas,
                    body.position,
                    renderRadius,
                    color = props.color,
                    strokeWidth = options.strokeWidth,
                    style = DrawStyle.Wireframe,
                    alpha = options.colorOptions.foregroundAlpha,
                )
            }

            else -> delegate.drawCircle(
                canvas,
                body.position,
                renderRadius,
                color = props.color,
                strokeWidth = options.strokeWidth,
                style = options.drawStyle,
                alpha = options.colorOptions.foregroundAlpha,
            )
        }
    }

    private fun getRenderRadius(body: Body): Distance? {
        val ageMillis = body.age.inWholeMilliseconds

        val radiusMultiplier = when {
            ageMillis > EnterAnimationMillis -> 1f
            else -> easeRadius(ageMillis.toFloat() / EnterAnimationMillis.toFloat())
        }
        if (radiusMultiplier == 0f) return null
        return body.radius * radiusMultiplier
    }

    private fun easeRadius(value: Float): Float = when {
        value < 0.5f -> (1f - sqrt(1f - (2f * value).pow(2f))) / 2f
        else -> (sqrt(1f - (-2f * value + 2f).pow(2f)) + 1f) / 2f
    }
}
