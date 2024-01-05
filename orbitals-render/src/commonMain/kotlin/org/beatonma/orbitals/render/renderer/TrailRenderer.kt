package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitals.render.options.CapStyle

class TrailRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val bodyPaths: MutableMap<UniqueID, MutableList<Position>> = mutableMapOf()
    private var trailTicks = 0
    private val trailTickFrequency = 0

    val maxAlpha: Float = .2f
    var traceThickness: Float = options.strokeWidth
        private set

    override var options: VisualOptions = options
        set(value) {
            field = value
            maxPoints = value.traceLineLength
            traceThickness = value.strokeWidth
        }

    private var maxPoints: Int = options.traceLineLength

    override fun onBodyCreated(body: Body) {
        super.onBodyCreated(body)
        bodyPaths[body.id] = mutableListOf()
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        bodyPaths.remove(body.id)
    }

    override fun drawBackground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {
        val remember = trailTicks++ > trailTickFrequency

        bodies.forEach { body ->
            if (remember && body is InertialBody) {
                remember(body)
            }
            val props = bodyProps[body.id] ?: return@forEach
            drawBody(canvas, body, props)
        }
        if (remember) {
            trailTicks = 0
        }
    }

    private fun remember(body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("remember $body no path")

        points.add(body.position.copy())
        while (points.size > maxPoints) {
            points.removeAt(0)
        }
    }

    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")
        val size = points.size

        for (index in 0 until size) {
            when (index) {
                0, size - 1 -> delegate.drawCircle(
                    canvas,
                    color = props.color,
                    position = points[index],
                    radius = maxOf(1f, traceThickness, body.radius.value / 10f).metres,
                    alpha = (index.toFloat() / size.toFloat()) * maxAlpha,
                    strokeWidth = traceThickness,
                    style = DrawStyle.Solid
                )
                else -> {
                    delegate.drawLine(
                        canvas,
                        color = props.color,
                        start = points[index -1],
                        end = points[index],
                        strokeWidth = maxOf(1f, traceThickness, body.radius.value / 10f),
                        cap = CapStyle.Round,
                        alpha = (index.toFloat() / size.toFloat()) * maxAlpha,
                    )
                }
            }
        }
    }
}
