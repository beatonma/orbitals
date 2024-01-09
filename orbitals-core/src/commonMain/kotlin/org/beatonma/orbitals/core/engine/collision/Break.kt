package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.times
import kotlin.random.Random

/**
 * See [CollisionStyle.Break].
 */
internal val BreakCollision = Collision { larger, smaller, changes ->
    if (smaller.mass < 5.kg) {
        return@Collision changes.remove(smaller.id)
    }

    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    if (overlapAmount <= 0f) return@Collision null

    val totalMass = larger.mass + smaller.mass
    val angle = larger.position.angleTo(smaller.position)
    val collisionPoint = larger.position + (angle * (distance - smaller.radius))

    val massRatio = smaller.mass / totalMass
    val ejectaMass = totalMass * overlapAmount
    val massFromSmaller = massRatio * ejectaMass
    val massFromLarger = ejectaMass - massFromSmaller

    larger.updateMassAndSize(larger.mass - massFromLarger)
    smaller.updateMassAndSize(smaller.mass - massFromSmaller)

    changes.add(
        createEjecta(massFromSmaller) {
            Motion(
                position = collisionPoint,
                velocity = smaller.velocity
            )
        } + createEjecta(massFromLarger) {
            Motion(
                position = collisionPoint,
                velocity = larger.velocity
            )
        }
    )
}

private fun createEjecta(totalMass: Mass, motion: () -> Motion): List<Body> {
    return totalMass.value.divideUnevenly(Random.nextInt(1, 5))
        .map(::Mass)
        .map { mass ->
            InertialBody(
                mass = mass,
                motion = motion(),
            )
        }
}
