package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.engine.overlapOf
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Momentum
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.degrees
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.physics.randomChoice
import org.beatonma.orbitals.core.physics.rotateBy
import org.beatonma.orbitals.core.physics.times
import kotlin.random.Random


/**
 * See [CollisionStyle.Break].
 */
internal val BreakCollision = Collision { larger, smaller, changes ->
    if (smaller.mass < Config.MinObjectMass) {
        return@Collision changes.remove(smaller.id)
    }

    val overlapAmount = overlapOf(larger, smaller)

    if (overlapAmount > .75f) return@Collision MergeCollision(larger, smaller, changes)

    val angle = smaller.position.angleTo(larger.position)
    val distance = larger.distanceTo(smaller)
    val collisionPoint = smaller.position + (angle * (distance - larger.radius))

    val ejectaMass = overlapAmount * smaller.mass
    val ejectaMomentum = ejectaMass * smaller.velocity
    val ejecta = createEjecta(collisionPoint, ejectaMass, smaller.density, ejectaMomentum)

    when {
        ejecta.isEmpty() -> null
        else -> {
            smaller.updateMassAndSize(smaller.mass - ejectaMass)
            changes + ejecta
        }
    }
}


private fun createEjecta(
    origin: Position,
    ejectaMass: Mass,
    density: Density,
    totalMomentum: Momentum,
): List<Body> {
    val ejectaCount = Random.nextInt(2, 20)

    // Artificially reduce mass so we can dump more momentum into velocity.
    val mass = ejectaMass / (ejectaCount * 2)
    if (mass < Config.MinObjectMass) return emptyList()

    return totalMomentum.divideUnevenly(ejectaCount).map { momentum ->
        InertialBody(
            mass,
            density,
            motion = Motion(
                position = origin,
                velocity = (momentum / mass).rotateBy(
                    (randomChoice(0, 180) + Random.nextInt(
                        -80,
                        80
                    )).degrees
                )
            )
        )
    }
}
