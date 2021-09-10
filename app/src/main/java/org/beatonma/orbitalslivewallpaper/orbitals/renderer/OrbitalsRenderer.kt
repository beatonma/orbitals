package org.beatonma.orbitalslivewallpaper.orbitals.renderer

import org.beatonma.orbitals.RectangleSpace
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions

interface OrbitalsRenderer<Canvas> {
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
    fun onSizeChanged(space: RectangleSpace) {}
    fun recycle() {}
}
