package org.beatonma.orbitals

import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.options.SystemGenerator
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.contains
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


interface OrbitalsEngine {
    var space: Space
    val physics: PhysicsOptions
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

    fun generateBodies(space: Space = this.space): List<Body> {
        val newBodies = physics.systemGenerators
            .filter { generator ->
                if (bodies.count { it is FixedBody } > 4) {
                    generator == SystemGenerator.Randomized
                }
                else true
            }
            .random()
            .generate(space)

        onBodiesCreated(newBodies)

        return newBodies
    }

    @OptIn(ExperimentalTime::class)
    fun tick(timeDelta: Duration) {
        if (bodyCount > 1) {
            bodies.forEachIndexed { index, body ->
                for (i in (index + 1) until bodyCount) {
                    val other = bodies[i]
                    body.applyGravity(other, timeDelta)
                    other.applyGravity(body, timeDelta)
                }
                body.applyInertia(timeDelta)
            }
        } else {
            bodies.forEach { body -> body.applyInertia(timeDelta) }
        }

        if (bodyCount < physics.maxEntities && Random.nextFloat() > .95f) {
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

//    @OptIn(ExperimentalTime::class)
//    private fun applyInertia(body: Body) = body.applyInertia(tickTimeDelta)

    fun reset() {
        bodies = listOf()
    }
}
