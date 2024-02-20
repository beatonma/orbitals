package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.BodyState
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitals.render.options.VisualOptions

class TrailRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val bodyPaths: MutableMap<UniqueID, MutableList<Position>> = mutableMapOf()

    val maxAlpha: Float = .3f
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

    override fun onBodyDestroyed(id: UniqueID) {
        super.onBodyDestroyed(id)
        bodyPaths.remove(id)
    }

    override fun drawForeground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {}
    override fun drawBackground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {
        bodies.fastForEach { body ->
            if (body.state >= BodyState.Supernova) {
                println(body.state)
                return@fastForEach
            }
            if (body is InertialBody) {
                remember(body)
            }
            val props = bodyProps[body.id] ?: return@fastForEach
            drawBody(canvas, body, props)
        }
    }

    private fun remember(body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("remember $body no path")

        points.add(body.position)
        while (points.size > maxPoints) {
            points.removeAt(0)
        }
    }

    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")
        val size = points.size

        for (index in 0 until size) {
            when (index) {
                0 -> {}

                size - 1 -> delegate.drawLine(
                    canvas,
                    color = props.color.withOpacity((index.toFloat() / size.toFloat()) * maxAlpha),
                    start = points[index],
                    end = body.position,
                    strokeWidth = maxOf(1f, traceThickness, body.radius.value / 10f),
                )

                else -> delegate.drawLine(
                    canvas,
                    color = props.color.withOpacity((index.toFloat() / size.toFloat()) * maxAlpha),
                    start = points[index - 1],
                    end = points[index],
                    strokeWidth = maxOf(1f, traceThickness, body.radius.value / 10f),
                )
            }
        }
    }
}
