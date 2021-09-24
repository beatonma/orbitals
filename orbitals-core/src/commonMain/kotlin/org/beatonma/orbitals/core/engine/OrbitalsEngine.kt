package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.ZeroAcceleration
import org.beatonma.orbitals.core.physics.contains
import org.beatonma.orbitals.core.physics.inContactWith
import org.beatonma.orbitals.core.physics.toInertialBody
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private val BodySortBy = Body::mass


interface OrbitalsEngine {
    var space: Universe
    var physics: PhysicsOptions
    var bodies: List<Body>
    val bodyCount: Int get() = bodies.size
    val visibleBodyCount: Int get() = bodies.count { space.contains(it.position) }
    var pruneCounter: Int
    val pruneFrequency: Int

    fun onBodiesCreated(newBodies: List<Body>) {}
    fun onBodyDestroyed(body: Body) {}

    fun addBodies(space: Space = this.space.visibleSpace) {
        val newBodies = generateBodies(space)
        bodies = (bodies + newBodies).sortedBy(BodySortBy)
        onBodiesCreated(newBodies)
    }

    fun addBody(body: Body) {
        bodies = (bodies + body).sortedBy(BodySortBy)
        onBodiesCreated(listOf(body))
    }

    fun removeBody(id: UniqueID) {
        bodies = bodies.filter { it.id != id }.sortedBy(BodySortBy)
    }

    private fun removeBody(body: Body) = removeBody(body.id)

    @OptIn(ExperimentalTime::class)
    fun tick(timeDelta: Duration) {
        val addedBodies = mutableListOf<Body>()
        val removedBodies = mutableListOf<Body>()

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
            }
        }

        addedBodies.forEach(::addBody)
        removedBodies.forEach(::removeBody)

        autoAddBodies()
        prune()
    }

    fun generateBodies(
        space: Space = this.space,
    ): List<Body> =
        physics.systemGenerators
            .random()
            .generate(space, bodies, physics)

    private fun onCollision(body: Body, other: Body): CollisionResults? {
        val bodies = arrayOf(body, other)
        bodies.sortByDescending(Body::mass)

        return applyCollision(bodies[0], bodies[1], physics.collisionStyle)
    }

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
        bodies = keep
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
