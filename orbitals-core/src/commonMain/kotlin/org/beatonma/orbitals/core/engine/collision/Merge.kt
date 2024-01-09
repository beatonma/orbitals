package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.ZeroMass
import org.beatonma.orbitals.core.physics.kg


/**
 * See [CollisionStyle.Merge].
 */
internal val MergeCollision = Collision { larger, smaller, changes ->
    require(larger.mass >= smaller.mass) {
        "MergeCollision Mass order wrong?! ${larger.mass} vs ${smaller.mass}"
    }
    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    val totalMass = larger.mass + smaller.mass

    val transferredMomentum = smaller.momentum * overlapAmount
    val transferredMass = when {
        smaller.mass == larger.mass -> ZeroMass
        transferredMomentum.magnitude.isZero() -> (larger.mass - smaller.mass) * .05f
        else -> transferredMomentum / smaller.velocity
    }

    when {
        transferredMass >= smaller.mass -> changes.remove(smaller.id)
        smaller.mass - transferredMass <= 1.kg -> changes.remove(smaller.id)
        else -> {
            val largeMomentum = larger.momentum + transferredMomentum

            larger.updateMassAndSize(larger.mass + transferredMass)
            smaller.updateMassAndSize(totalMass - larger.mass)

            larger.velocity = largeMomentum / larger.mass

            null
        }
    }
}
