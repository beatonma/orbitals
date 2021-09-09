package org.beatonma.orbitalslivewallpaper.orbitals.renderer

import org.beatonma.orbitals.RectangleSpace
import org.beatonma.orbitals.physics.Body

interface OrbitalsRenderer<Canvas> {
    fun drawBody(canvas: Canvas, body: Body)

    fun drawBackground(canvas: Canvas, bodies: List<Body>) {}
    fun drawForeground(canvas: Canvas, bodies: List<Body>) {
        bodies.forEach { body ->
            drawBody(canvas, body)
        }
    }

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(body: Body) {}
    fun reset(space: RectangleSpace) {}
    fun recycle() {}
}
