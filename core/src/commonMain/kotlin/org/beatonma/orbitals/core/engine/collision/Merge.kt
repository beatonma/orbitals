package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.engine.overlapOf
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.ZeroMass
import org.beatonma.orbitals.core.physics.ZeroVelocity
import org.beatonma.orbitals.core.physics.kg


/**
 * See [CollisionStyle.Merge].
 */
internal val MergeCollision = Collision { larger, smaller, changes ->
    val overlapAmount = overlapOf(larger, smaller)
    val totalMass = larger.mass + smaller.mass

    val transferredMomentum = smaller.momentum * overlapAmount

    if (transferredMomentum.magnitude.isZero()) {
        return@Collision NoMomentumMerge(larger, smaller, changes)
    }

    val largeMomentum = larger.momentum + transferredMomentum
    val smallMomentum = smaller.momentum - transferredMomentum

    larger.updateMassAndSize(larger.mass + transferredMomentum / smaller.velocity)
    larger.velocity = largeMomentum / larger.mass

    smaller.updateMassAndSize(maxOf(0f.kg, totalMass - larger.mass))
    smaller.velocity = when {
        smaller.mass == ZeroMass -> ZeroVelocity
        else -> smallMomentum / smaller.mass
    }

    null
}

private val NoMomentumMerge = Collision { larger, smaller, changes ->
    val overlapAmount = overlapOf(larger, smaller)

    val transferredMass = smaller.mass * overlapAmount

    larger.updateMassAndSize(larger.mass + transferredMass)
    smaller.updateMassAndSize(smaller.mass - transferredMass)

    when {
        smaller.mass < 1f.kg -> changes.remove(smaller.id)
        else -> null
    }
}
