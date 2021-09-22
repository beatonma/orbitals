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
import org.beatonma.orbitals.core.physics.toInertialBody
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


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
//        .also {
//        if (it.second.isEmpty()) return@also
//        println(space)
//        println("KEEP")
//        it.first.forEach { body ->
//            println(body.position)
//        }
//
//        println("DESTROY")
//        it.second.forEach { body ->
//            println(body.position)
//        }
//        println(".")
//        println(".")
//    }
}
