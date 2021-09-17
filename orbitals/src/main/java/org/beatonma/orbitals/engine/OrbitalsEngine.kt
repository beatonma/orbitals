package org.beatonma.orbitals.engine

import androidx.annotation.VisibleForTesting
import org.beatonma.orbitals.chance
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.percent
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.GreatAttractor
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitals.physics.ZeroAcceleration
import org.beatonma.orbitals.physics.contains
import org.beatonma.orbitals.physics.toInertialBody
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface AllowOutOfBounds


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

    fun addBody(body: Body) {
        bodies = bodies + body
        onBodiesCreated(listOf(body))
    }

    fun removeBody(id: UniqueID) {
        bodies = bodies.filter { it.id != id }
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

        autoAddBodies()
        prune()
    }

    private fun autoAddBodies() {
        if (physics.autoAddBodies) {
            if (bodyCount == 0
                || (bodyCount < physics.maxEntities && chance(1.percent))
            ) {
                addBodies()
            }
        }
    }

    private fun prune() {
        if (pruneCounter++ > pruneFrequency) {
            pruneBodies()
            pruneCounter = 0
        }
    }

    /**
     * Remove any bodies that leave the active region.
     * Any FixedBody that exceeds a maximum age is converted to an InertialBody so that it might be
     * expelled by later interactions.
     */
    @OptIn(ExperimentalTime::class)
    fun pruneBodies(space: Universe = this.space) {
        val (keep, destroy) = pruneBodies(bodies, space, physics.maxFixedBodyAgeMinutes)
        destroy.forEach(::onBodyDestroyed)
        bodies = keep
    }

    fun clear() {
        val purge = bodies.toList()
        purge.forEach(this::onBodyDestroyed)
        bodies = listOf()
    }
}


@OptIn(ExperimentalTime::class)
@VisibleForTesting
internal fun pruneBodies(
    bodies: List<Body>,
    space: Space,
    ageLimit: Duration,
    keepAgedRandomizer: () -> Boolean = { chance(10.percent) }
): Pair<List<Body>, List<Body>> {
    val (keep, furtherAction) = bodies.partition {
        when (it) {
            is FixedBody -> {
                it.age < ageLimit || keepAgedRandomizer()
            }
            is InertialBody -> {
                space.contains(it.position)
            }
            is GreatAttractor -> {
                it.age < ageLimit || keepAgedRandomizer()
            }
        }
    }

    val (toBeConverted, toDestroy) = furtherAction.partition {
        it is FixedBody
    }

    return Pair(
        keep + (toBeConverted as List<FixedBody>).map(FixedBody::toInertialBody),
        toDestroy
    )
}
