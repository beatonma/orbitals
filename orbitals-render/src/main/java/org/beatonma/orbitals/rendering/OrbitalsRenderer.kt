package org.beatonma.orbitals.rendering

import org.beatonma.orbitals.engine.Space
import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body

interface OrbitalsRenderer<Canvas> {
    val delegate: CanvasDelegate<Canvas>
    var options: VisualOptions

    fun drawBody(canvas: Canvas, body: Body)

    fun drawBackground(canvas: Canvas, bodies: List<Body>) {}
    fun drawForeground(canvas: Canvas, bodies: List<Body>) {
        bodies.forEach { body ->
            drawBody(canvas, body)
        }
    }

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(body: Body) {}
    fun onSizeChanged(space: Space) {}
    fun recycle() {}
}
