package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.fastForEachIndexed
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.BodyState
import org.beatonma.orbitals.core.physics.Fixed
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.contains
import org.beatonma.orbitals.core.physics.toInertialBody
import kotlin.time.Duration

private val BodySortBy = Body::mass

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

    fun generateBodies(space: Space = this.space): List<Body> {
        return physics.systemGenerators
            .run {
                when (space) {
                    this@OrbitalsEngine.space -> this
                    else -> filterNot { it == SystemGenerator.GreatAttractor }
                }
            }
            .random()
            .generate(space, bodies, physics)
    }
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
        when {
            bodies.isEmpty() -> return

            bodyCount + bodies.size > physics.maxEntities -> {
                add(bodies.take((physics.maxEntities - bodyCount).coerceAtLeast(0)))
            }

            else -> {
                this.bodies += bodies
                super.add(bodies)
            }
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
        bodies.fastForEach {
            it.tick(timeDelta)
        }

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

            checkEndOfLife(body)
        }

        remove(removedBodyIds)
        removedBodyIds.clear()

        add(addedBodies)
        addedBodies.clear()

        prune()
        autoAddBodies()
    }

    /**
     * Destroy or change state if [body] has breached any end-of-life conditions.
     */
    private fun checkEndOfLife(body: Body) {
        if (body.mass < Config.MinObjectMass) {
            removedBodyIds += body.id
            return
        }

        if (body.stateEvent() == BodyState.Supernova) {
            addedBodies += body.explodeSupernova()
            return
        }

        if (body is Inertial && body.mass > Config.MaxObjectMass) {
            body.collapse()
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

    /**
     * Remove any bodies that leave the active region.
     * Any FixedBody that exceeds a maximum age is converted to an InertialBody so that it might be
     * expelled by later interactions.
     */
    private fun prune() {
        if (pruneCounter++ > pruneFrequency) {
            val (keep, destroy) = pruneBodies(bodies, space, physics.minFixedBodyAge)
            destroy.fastForEach { onBodyDestroyed(it.id) }
            bodies = keep
            pruneCounter = 0
        }
    }
}

internal fun pruneBodies(
    bodies: List<Body>,
    space: Space,
    ageLimit: Duration,
    keepAgedRandomizer: () -> Boolean = { chance(10.percent) }
): Pair<List<Body>, List<Body>> {
    val (keep, furtherAction) = bodies.partition {
        if (it.isImmortal) {
            true
        } else when (it) {
            is Inertial -> {
                space.contains(it.position)
            }

            is Fixed -> {
                it.age < ageLimit || keepAgedRandomizer()
            }
        }
    }

    val (toBeConverted, toBeDestroyed) = furtherAction.partition {
        it is FixedBody
    }

    return Pair(
        keep + toBeConverted.map(Body::toInertialBody),
        toBeDestroyed
    )
}
