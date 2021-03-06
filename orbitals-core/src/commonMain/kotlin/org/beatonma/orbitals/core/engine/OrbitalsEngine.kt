package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.ZeroAcceleration
import org.beatonma.orbitals.core.physics.contains
import org.beatonma.orbitals.core.physics.inContactWith
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.toInertialBody
import org.beatonma.orbitals.core.util.timeIt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private val BodySortBy = Body::mass

@OptIn(ExperimentalTime::class)
private val BodyMassLimit = 1500.kg


interface OrbitalsEngine {
    var space: Universe
    var physics: PhysicsOptions
    var bodies: List<Body>
    val bodyCount: Int get() = bodies.size
    var pruneCounter: Int
    val pruneFrequency: Int

    val addedBodies: MutableList<Body>
    val removedBodies: MutableList<Body>

    fun onBodiesCreated(newBodies: List<Body>) {}
    fun onBodyDestroyed(body: Body) {}

    fun addBodies(space: Space = this.space.visibleSpace) {
        val newBodies = generateBodies(space)
        setBodies(bodies + newBodies)
        onBodiesCreated(newBodies)
    }

    fun addBody(body: Body) {
        setBodies(bodies + body)
        onBodiesCreated(listOf(body))
    }

    fun removeBody(id: UniqueID) {
        setBodies(bodies.filter { it.id != id })
    }

    private fun removeBody(body: Body) = removeBody(body.id)

    @OptIn(ExperimentalTime::class)
    fun tick(timeDelta: Duration) {
        val duration = timeIt(enabled = true) {
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

                        if (body.inContactWith(other)) {
                            val result = onCollision(body, other)

                            if (result != null) {
                                val (added, removed) = result
                                addedBodies += added
                                removedBodies += removed
                            }
                        }
                    }

                    if (body is Inertial && body.mass > BodyMassLimit) {
                        val (added, removed) = body.explode()
                        addedBodies += added
                        removedBodies += removed
                    }
                }
            }

            addedBodies.forEach(::addBody)
            removedBodies.forEach(::removeBody)

            addedBodies.clear()
            removedBodies.clear()
        }

        if (duration < 15) {
            autoAddBodies()
        }
        prune()
    }

    fun generateBodies(
        space: Space = this.space,
    ): List<Body> =
        physics.systemGenerators
            .random()
            .generate(space, bodies, physics)

    private fun setBodies(_bodies: List<Body>) {
        bodies = _bodies.sortedByDescending(BodySortBy)
    }

    private fun onCollision(body: Body, other: Body): CollisionResults? =
        applyCollision(body, other, physics.collisionStyle)

    private fun autoAddBodies() {
        if (bodyCount == 0
            || (physics.autoAddBodies
                    && bodyCount < physics.maxEntities
                    && chance(1.percent))
        ) {
            addBodies()
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
    private fun pruneBodies(space: Universe = this.space) {
        val (keep, destroy) = pruneBodies(bodies, space, physics.maxFixedBodyAge)
        destroy.forEach(::onBodyDestroyed)
        setBodies(keep)
    }

    fun clear() {
        val purge = bodies.toList()
        purge.forEach(this::onBodyDestroyed)
        bodies = listOf()
    }
}

@OptIn(ExperimentalTime::class)
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
