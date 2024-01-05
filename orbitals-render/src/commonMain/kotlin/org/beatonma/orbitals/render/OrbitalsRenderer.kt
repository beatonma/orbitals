package org.beatonma.orbitals.render

import org.beatonma.orbitals.core.engine.Space
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.renderer.BodyProperties
import org.beatonma.orbitals.render.renderer.BodyPropertyMap

interface OrbitalsRenderer<Canvas> {
    val delegate: CanvasDelegate<Canvas>
    var options: VisualOptions

    fun drawBody(canvas: Canvas, body: Body, props: BodyProperties)

    fun drawBackground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {}
    fun drawForeground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {
        bodies.forEach { body ->
            val props = bodyProps[body.id] ?: return@forEach
            drawBody(canvas, body, props)
        }
    }

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(body: UniqueID) {}
    fun onSizeChanged(space: Space) {}
    fun recycle() {}

    fun onBodyDestroyed(body: Body) = onBodyDestroyed(body.id)
}
