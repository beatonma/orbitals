package org.beatonma.orbitals

import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.ZeroAcceleration
import org.beatonma.orbitals.physics.contains
import org.beatonma.orbitals.physics.toInertialBody
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


interface OrbitalsEngine {
    var space: Universe
    var physics: PhysicsOptions
    var bodies: List<Body>
    val bodyCount: Int get() = bodies.size
    var pruneCounter: Int
    val pruneFrequency: Int

    @OptIn(ExperimentalTime::class)
    val tickTimeDelta: Duration

    fun onBodiesCreated(newBodies: List<Body>) {}
    fun onBodyDestroyed(body: Body) {}

    fun addBodies(space: Space = this.space.visibleSpace) {
        bodies = bodies + generateBodies(space)
    }

    fun generateBodies(
        space: Space = this.space,
    ): List<Body> {
        val newBodies = physics.systemGenerators
            .random()
            .generate(space, bodies, physics)

        onBodiesCreated(newBodies)

        return newBodies
    }

    @OptIn(ExperimentalTime::class)
    fun tick(timeDelta: Duration) {
        bodies.forEach {
            it.motion.acceleration = ZeroAcceleration
            it.tick(timeDelta)
        }

        if (bodyCount > 1) {
            bodies.forEachIndexed { index, body ->
                for (i in (index + 1) until bodyCount) {
                    val other = bodies[i]
                    body.applyGravity(other, timeDelta, G = physics.G)
                    other.applyGravity(body, timeDelta, G = physics.G)
                }
            }
        }

        if (physics.autoAddBodies
            && bodyCount < physics.maxEntities
            && chance(1.percent)
        ) {
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
    @OptIn(ExperimentalTime::class)
    @Suppress("UNCHECKED_CAST")
    private fun pruneBodies(space: Universe = this.space) {
        val (keep, escaped) = bodies.partition { body -> space.contains(body.position) }
        val (oldAged, youngEnough) =
            keep.partition { it is FixedBody && it.age > physics.maxFixedBodyAgeMinutes }

        bodies = youngEnough + (oldAged as List<FixedBody>).map(FixedBody::toInertialBody)

        escaped.forEach(::onBodyDestroyed)
    }

    fun clear() {
        val purge = bodies.toList()
        purge.forEach(this::onBodyDestroyed)
        bodies = listOf()
    }
}
