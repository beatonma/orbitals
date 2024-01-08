package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.kg


/**
 * See [CollisionStyle.Merge].
 */
internal val MergeCollision = Collision { larger, smaller, changes ->
    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    val totalMass = larger.mass + smaller.mass

    val transferredMomentum = smaller.momentum * overlapAmount
    val transferredMass = when (smaller.velocity.isZero()) {
        true -> {
            // Velocity is zero, momentum is zero -> arbitrary mass transfer
            (larger.mass - smaller.mass) * .05f
        }

        false -> transferredMomentum / smaller.velocity
    }
    val remainingMass = smaller.mass - transferredMass

    if (remainingMass <= 1.kg) {
        // If remaining mass is very small, destroy the object.
        changes.remove(smaller)
    } else {
        val largeMomentum = larger.momentum + transferredMomentum

        larger.updateMassAndSize(larger.mass + transferredMass)
        smaller.updateMassAndSize(totalMass - larger.mass)

        larger.velocity = largeMomentum / larger.mass

        null
    }
}
