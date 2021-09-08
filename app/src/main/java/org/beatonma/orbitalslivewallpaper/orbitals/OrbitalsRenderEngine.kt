package org.beatonma.orbitalslivewallpaper.orbitals

import org.beatonma.orbitals.OrbitalsEngine
import org.beatonma.orbitals.Space
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.OrbitalsRenderer
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OrbitalsRenderEngine<T>(
    val renderers: List<OrbitalsRenderer<T>>,
    options: Options,
    tickDelta: Duration = Duration.seconds(1),
) {
    @OptIn(ExperimentalTime::class)
    val engine: OrbitalsEngine = object: OrbitalsEngine {
        override var space: Space = Space(1, 1)
        override val physics: PhysicsOptions = options.physics
        override var bodies: List<Body> = listOf()
        override val tickTimeDelta: Duration = tickDelta

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

    fun onSizeChanged(width: Int, height: Int) {
        engine.space = Space(width, height)
        renderers.forEach { it.reset(engine.space.visibleSpace) }
    }

    fun update(canvas: T) {
        engine.tick()

        renderers.forEach {
            it.drawBackground(canvas, engine.bodies)
        }

        renderers.forEach {
            it.drawForeground(canvas, engine.bodies)
        }
    }

    fun reset() {
        engine.reset()
        renderers.forEach { it.reset(engine.space) }
    }

    fun recycle() {
        renderers.forEach(OrbitalsRenderer<T>::recycle)
    }
}
