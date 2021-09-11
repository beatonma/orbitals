package org.beatonma.orbitals

import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.ZeroAcceleration
import org.beatonma.orbitals.physics.contains
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


interface OrbitalsEngine {
    var space: Space
    var physics: PhysicsOptions
    var bodies: List<Body>
    val bodyCount: Int get() = bodies.size
    var pruneCounter: Int
    val pruneFrequency: Int

    @OptIn(ExperimentalTime::class)
    val tickTimeDelta: Duration

    fun onBodiesCreated(newBodies: List<Body>) {}
    fun onBodyDestroyed(body: Body) {}

    fun addBodies(space: Space = this.space) {
        bodies = bodies + generateBodies(space)
    }

    fun generateBodies(
        space: Space = this.space,
        max: Int = physics.maxEntities - bodies.size
    ): List<Body> {
        if (max <= 0) return listOf()

        val newBodies = physics.systemGenerators
            .random()
            .generate(space, bodies, max - bodies.size)

        onBodiesCreated(newBodies)

        return newBodies
    }

    @OptIn(ExperimentalTime::class)
    fun tick(timeDelta: Duration) {
        bodies.forEach {
            it.acceleration = ZeroAcceleration
            it.tick(timeDelta)
        }

        if (bodyCount > 1) {
            bodies.forEachIndexed { index, body ->
                for (i in (index + 1) until bodyCount) {
                    val other = bodies[i]
                    body.applyGravity(other, timeDelta)
                    other.applyGravity(body, timeDelta)
                }
            }
        }

        if (bodyCount < physics.maxEntities && chance(5.percent)) {
            addBodies()
        }

        if (pruneCounter++ > pruneFrequency) {
            pruneBodies()
            pruneCounter = 0
        }
    }

    /**
     * Remove any bodies that leave the active region.
     */
    private fun pruneBodies(space: Space = this.space) {
        val (keep, purge) = bodies.partition { body -> space.contains(body.position) }
        bodies = keep
        purge.forEach(::onBodyDestroyed)
    }

    fun clear() {
        val purge = bodies.toList()
        purge.forEach(this::onBodyDestroyed)
        bodies = listOf()
    }
}
