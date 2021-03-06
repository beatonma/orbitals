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

class TrailRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    val bodyPaths: MutableMap<UniqueID, MutableList<Position>> = mutableMapOf()
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

    override fun drawForeground(canvas: Canvas, bodies: List<Body>) {

    }

    override fun drawBackground(canvas: Canvas, bodies: List<Body>) {
        val remember = trailTicks++ > trailTickFrequency

        bodies.forEach { body ->
            if (remember && body is InertialBody) {
                remember(body)
            }
            drawBody(canvas, body)
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

    override fun drawBody(canvas: Canvas, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            delegate.drawCircle(
                canvas,
                color = 0x00ff00,
                position = position,
                radius = maxOf(1f, maxOf(traceThickness, body.radius.value / 10f)).metres,
                alpha = (index.toFloat() / points.size.toFloat()) * maxAlpha,
                strokeWidth = options.strokeWidth,
                style = DrawStyle.Solid,
            )
        }
    }
}
