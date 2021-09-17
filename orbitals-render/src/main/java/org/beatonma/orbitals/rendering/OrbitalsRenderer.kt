package org.beatonma.orbitals.rendering

import org.beatonma.orbitals.engine.Space
import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID

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
    fun onBodyDestroyed(body: UniqueID) {}
    fun onSizeChanged(space: Space) {}
    fun recycle() {}

    fun onBodyDestroyed(body: Body) = onBodyDestroyed(body.id)
}
