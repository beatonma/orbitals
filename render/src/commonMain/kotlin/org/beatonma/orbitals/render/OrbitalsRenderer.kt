package org.beatonma.orbitals.render

import org.beatonma.orbitals.core.engine.Space
import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.renderer.BodyProperties
import org.beatonma.orbitals.render.renderer.BodyPropertyMap

interface OrbitalsRenderer<Canvas> {
    val delegate: CanvasDelegate<Canvas>
    var options: VisualOptions

    fun drawBody(canvas: Canvas, body: Body, props: BodyProperties)

    fun drawBackground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap)
    fun drawForeground(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap)

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(id: UniqueID) {}
    fun onSizeChanged(space: Space) {}
    fun recycle() {}

    fun drawBodies(canvas: Canvas, bodies: List<Body>, bodyProps: BodyPropertyMap) {
        bodies.fastForEach { body ->
            val props = bodyProps[body.id] ?: return@fastForEach
            drawBody(canvas, body, props)
        }
    }
}
