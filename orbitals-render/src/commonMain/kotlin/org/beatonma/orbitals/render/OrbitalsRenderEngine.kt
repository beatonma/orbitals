package org.beatonma.orbitals.render

import org.beatonma.orbitals.core.engine.DefaultOrbitalsEngine
import org.beatonma.orbitals.core.engine.Space
import org.beatonma.orbitals.core.engine.Universe
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.renderer.BodyProperties
import kotlin.time.Duration

class OrbitalsRenderEngine<Canvas>(
    private val canvasDelegate: CanvasDelegate<Canvas>,
    options: Options,
) {
    var options: Options = options
        set(value) {
            field = value
            engine.physics = value.physics
            renderers = diffRenderers(value)
            renderers.forEach { it.options = value.visualOptions }
        }
    var renderers: Set<OrbitalsRenderer<Canvas>> =
        getRenderers(options.visualOptions, canvasDelegate)

    private val bodyProps = mutableMapOf<UniqueID, BodyProperties>()
    private val engine = object : DefaultOrbitalsEngine(options.physics) {
        override var space: Universe = Universe(1, 1)
            set(value) {
                field = value
                if (bodies.isEmpty() && value.isValid) {
                    addBodies(space.visibleSpace)
                }
            }

        override fun onBodyCreated(body: Body) {
            renderers.forEach { it.onBodyCreated(body) }
            bodyProps[body.id] = BodyProperties(
                this@OrbitalsRenderEngine.options.visualOptions.colorOptions.colorFor(body)
            )
        }

        override fun onBodyDestroyed(id: UniqueID) {
            renderers.forEach { it.onBodyDestroyed(id) }
            bodyProps.remove(id)
        }
    }

    val bodies get() = engine.bodies

    fun onSizeChanged(width: Int, height: Int) {
        if (width != engine.space.visibleSpace.width || height != engine.space.visibleSpace.height) {
            engine.space = Universe(width, height)
            renderers.forEach { it.onSizeChanged(engine.space.visibleSpace) }
        }
    }

    fun update(canvas: Canvas, delta: Duration) {
        engine.tick(delta)
        render(canvas)
    }

    fun render(canvas: Canvas) {
        renderers.forEach {
            it.drawBackground(canvas, engine.bodies, bodyProps)
        }

        renderers.forEach {
            it.drawForeground(canvas, engine.bodies, bodyProps)
        }
    }

    fun addBodies(space: Space = engine.space) {
        engine.addBodies(space)
    }

    fun add(body: Body) {
        engine.add(body)
    }

    fun add(bodies: List<Body>) {
        engine.add(bodies)
    }

    fun remove(id: UniqueID) {
        engine.remove(id)
    }

    fun remove(ids: List<UniqueID>) {
        engine.remove(ids)
    }

    fun clear() {
        engine.clear()
    }

    fun recycle() {
        renderers.forEach(OrbitalsRenderer<Canvas>::recycle)
    }

    private fun diffRenderers(options: Options) = diffRenderers(
        renderers,
        options.visualOptions.renderLayers,
        options.visualOptions,
        bodies,
        canvasDelegate,
    )
}
