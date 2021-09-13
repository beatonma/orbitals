package org.beatonma.orbitalslivewallpaper.orbitals.renderer.trail

import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.OrbitalsRenderer

abstract class BaseTrailRenderer<Canvas>(
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
        while(points.size > maxPoints) {
            points.removeAt(0)
        }
    }
}
