package org.beatonma.orbitalslivewallpaper.orbitals

import org.beatonma.orbitals.engine.OrbitalsEngine
import org.beatonma.orbitals.engine.Space
import org.beatonma.orbitals.engine.Universe
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OrbitalsRenderEngine<T>(
    var renderers: Set<org.beatonma.orbitals.rendering.OrbitalsRenderer<T>>,
    options: Options,
    private val onOptionsChange: OrbitalsRenderEngine<T>.(Options) -> Unit
) {
    var options: Options = options
        set(value) {
            field = value
            onOptionsChange(value)
            engine.physics = value.physics
            renderers.forEach { it.options = value.visualOptions }
        }

    @OptIn(ExperimentalTime::class)
    private val engine: OrbitalsEngine = object : OrbitalsEngine {
        override var space: Universe = Universe(1, 1)
        override var physics: PhysicsOptions = options.physics
        override var bodies: List<Body> = listOf()
        override val tickTimeDelta: Duration = physics.tickDelta

        override var pruneCounter = 0
        override val pruneFrequency = 60

        override fun onBodiesCreated(newBodies: List<Body>) {
            super.onBodiesCreated(newBodies)
            renderers.forEach { renderer -> newBodies.forEach(renderer::onBodyCreated) }
        }

        override fun onBodyDestroyed(body: Body) {
            super.onBodyDestroyed(body)
            renderers.forEach { it.onBodyDestroyed(body) }
        }
    }

    val bodies get() = engine.bodies

    fun onSizeChanged(width: Int, height: Int) {
        engine.space = Universe(width, height)
        renderers.forEach { it.onSizeChanged(engine.space.visibleSpace) }
    }

    fun update(canvas: T, delta: Duration = engine.tickTimeDelta) {
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

    fun clear() {
        engine.clear()
    }

    fun recycle() {
        renderers.forEach(org.beatonma.orbitals.rendering.OrbitalsRenderer<T>::recycle)
    }
}

inline fun <reified Canvas> diffRenderers(
    engine: OrbitalsRenderEngine<Canvas>,
) = org.beatonma.orbitals.rendering.diffRenderers(
    engine.renderers,
    engine.options.visualOptions.renderLayers,
    engine.options.visualOptions,
    engine.bodies,
)
