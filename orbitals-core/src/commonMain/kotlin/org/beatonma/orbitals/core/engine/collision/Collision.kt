package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.sizeOf

internal fun interface Collision {
    operator fun invoke(larger: Body, smaller: Body, changes: CollisionLog): CollisionResults?

    companion object {
        fun Body.updateMassAndSize(mass: Mass) {
            this.mass = mass
            this.radius = sizeOf(mass, this.density)
        }
    }
}

/**
 * A record of any bodies that need to be added or removed from the simulation following a collision..
 */
internal interface CollisionResults {
    val added: List<Body>
    val removed: List<UniqueID>

    operator fun component1() = added
    operator fun component2() = removed
}

internal interface CollisionLog : CollisionResults {
    fun add(body: Body): CollisionLog
    fun remove(body: UniqueID): CollisionLog

    fun add(bodies: List<Body>): CollisionLog
    fun remove(bodies: List<UniqueID>): CollisionLog

    fun clear()
}
