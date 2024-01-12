package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.OrbitalsBuildConfig
import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.engine.collision.CollisionResults
import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.fastForEachIndexed
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.contains
import org.beatonma.orbitals.core.physics.inContactWith
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.toInertialBody
import org.beatonma.orbitals.core.util.timeIt
import org.beatonma.orbitals.core.util.warn
import kotlin.time.Duration

private val BodySortBy = Body::mass

private val BodyMassLimit = 1500.kg

interface OrbitalsEngine {
    val space: Universe
    val physics: PhysicsOptions
    val bodies: List<Body>
    val bodyCount: Int get() = bodies.size

    /**
     * Apply physics for the given time step.
     */
    fun tick(timeDelta: Duration)

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(id: UniqueID) {}


    fun clear() {
        remove(bodies.map { it.id })
    }

    fun addBodies(space: Space = this.space) {
        add(generateBodies(space))
    }

    fun add(body: Body) {
        onBodyCreated(body)
    }

    fun add(bodies: List<Body>) {
        bodies.fastForEach(::onBodyCreated)
    }

    fun remove(body: Body) {
        onBodyDestroyed(body.id)
    }

    fun remove(id: UniqueID) {
        onBodyDestroyed(id)
    }

    fun remove(ids: List<UniqueID>) {
        ids.fastForEach(::onBodyDestroyed)
    }

    fun generateBodies(space: Space = this.space): List<Body> =
        physics.systemGenerators
            .random()
            .generate(space, bodies, physics)
}


open class DefaultOrbitalsEngine(override var physics: PhysicsOptions) : OrbitalsEngine {
    override var space: Universe = Universe(1, 1)

    /**
     * Ordered list of objects in the simulation, sorted by descending mass.
     */
    final override var bodies: List<Body> = listOf()
        private set(value) {
            field = value.sortedByDescending(BodySortBy)
        }
    private var pruneCounter: Int = 0
    private val pruneFrequency: Int = 60

    private val addedBodies: MutableList<Body> = mutableListOf()
    private val removedBodyIds: MutableList<UniqueID> = mutableListOf()

    final override fun add(body: Body) {
        this.bodies += body
        super.add(body)
    }

    final override fun add(bodies: List<Body>) {
        if (bodies.isNotEmpty()) {
            this.bodies += bodies
            super.add(bodies)
        }
    }

    final override fun remove(body: Body) {
        this.bodies = this.bodies.filterNot { it.id == body.id }
        super.remove(body)
    }

    final override fun remove(id: UniqueID) {
        this.bodies = this.bodies.filterNot { it.id == id }
        super.remove(id)
    }

    final override fun remove(ids: List<UniqueID>) {
        if (ids.isNotEmpty()) {
            this.bodies = this.bodies.filterNot { ids.contains(it.id) }
            super.remove(ids)
        }
    }

    override fun tick(timeDelta: Duration) {
        val duration = timeIt(enabled = OrbitalsBuildConfig.DEBUG) {
            bodies.fastForEach {
                it.tick(timeDelta)
            }

            if (bodyCount > 1) {
                bodies.fastForEachIndexed { index, body ->
                    for (i in (index + 1) until bodyCount) {
                        val other = bodies[i]
                        body.applyGravity(other, timeDelta, G = physics.G)
                        other.applyGravity(body, timeDelta, G = physics.G)

                        applyCollision(
                            body,
                            other,
                            physics.collisionStyle
                        )?.let { (added, removed) ->
                            addedBodies += added
                            removedBodyIds += removed
                        }
                    }

                    if (body is Inertial && body.mass > BodyMassLimit) {
                        val added = body.explode()
                        addedBodies += added
                        removedBodyIds += body.id
                    }
                }
            }

            add(addedBodies)
            addedBodies.clear()

            remove(removedBodyIds)
            removedBodyIds.clear()

            prune()
            autoAddBodies()
        }

        if (OrbitalsBuildConfig.DEBUG && duration > 15) {
            warn("Frame took ${duration}ms ($bodyCount objects)")
        }
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
    private fun pruneBodies(space: Universe = this.space) {
        val (keep, destroy) = pruneBodies(bodies, space, physics.maxFixedBodyAge)
        destroy.fastForEach { onBodyDestroyed(it.id) }
        bodies = keep
    }
}

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

    @Suppress("UNCHECKED_CAST")
    return Pair(
        keep + (toBeConverted as List<FixedBody>).map(FixedBody::toInertialBody),
        toDestroy
    )
}
