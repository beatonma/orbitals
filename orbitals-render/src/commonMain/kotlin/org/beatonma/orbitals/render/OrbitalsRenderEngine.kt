package org.beatonma.orbitals.render

import org.beatonma.orbitals.core.engine.OrbitalsEngine
import org.beatonma.orbitals.core.engine.Space
import org.beatonma.orbitals.core.engine.Universe
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.UniqueID
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OrbitalsRenderEngine<T>(
    var renderers: Set<OrbitalsRenderer<T>>,
    options: Options,
    private val onOptionsChange: OrbitalsRenderEngine<T>.(Options) -> Unit
) {
    var options: Options = options
        set(value) {
            field = value
            onOptionsChange(value)
            engine.physics = value.physics
            renderers.forEach { it.options = value.visualOptions }
            println(renderers)
        }

    @OptIn(ExperimentalTime::class)
    private val engine: OrbitalsEngine = object : OrbitalsEngine {
        override var space: Universe = Universe(1, 1)
        override var physics: PhysicsOptions = options.physics
        override var bodies: List<Body> = listOf()

        override var pruneCounter = 0
        override val pruneFrequency = 60
        override val addedBodies: MutableList<Body> = mutableListOf()
        override val removedBodies: MutableList<Body> = mutableListOf()

        override fun onBodiesCreated(newBodies: List<Body>) {
            super.onBodiesCreated(newBodies)
            renderers.forEach { renderer -> newBodies.forEach(renderer::onBodyCreated) }
        }

        override fun onBodyDestroyed(body: Body) {
            super.onBodyDestroyed(body)
            renderers.forEach { renderer -> renderer.onBodyDestroyed(body) }
        }
    }

    val bodies get() = engine.bodies

    fun onSizeChanged(width: Int, height: Int) {
        if (width != engine.space.visibleSpace.width || height != engine.space.visibleSpace.height) {
            engine.space = Universe(width, height)
            renderers.forEach { it.onSizeChanged(engine.space.visibleSpace) }
        }
    }

    fun update(canvas: T, delta: Duration) {
        engine.tick(delta)

        renderers.forEach {
            it.drawBackground(canvas, engine.bodies)
        }

        renderers.forEach {
            it.drawForeground(canvas, engine.bodies)
        }
    }

    fun addBodies(space: Space = engine.space) {
        engine.addBodies(space)
    }

    fun addBody(body: Body) {
        engine.addBody(body)
        renderers.forEach { it.onBodyCreated(body) }
    }

    fun removeBody(id: UniqueID) {
        engine.removeBody(id)
        renderers.forEach { it.onBodyDestroyed(id) }
    }

    fun clear() {
        engine.clear()
    }

    fun recycle() {
        renderers.forEach(OrbitalsRenderer<T>::recycle)
    }
}

inline fun <reified Canvas> diffRenderers(
    engine: OrbitalsRenderEngine<Canvas>,
    delegate: CanvasDelegate<Canvas>
) = diffRenderers(
    engine.renderers,
    engine.options.visualOptions.renderLayers,
    engine.options.visualOptions,
    engine.bodies,
    delegate,
)
