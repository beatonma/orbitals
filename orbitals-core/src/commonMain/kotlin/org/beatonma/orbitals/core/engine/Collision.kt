package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.sizeOf


internal fun applyCollision(
    larger: Body,
    smaller: Body,
    collisionStyle: CollisionStyle
): CollisionResults? {
    check(larger.mass >= smaller.mass) {
        "applyCollision expects bodies to be ordered by descending mass!"
    }

    return try {
        val f = when (collisionStyle) {
            CollisionStyle.None -> return null
            CollisionStyle.Merge -> ::applyMergeCollision
            CollisionStyle.Break -> ::applyBreakCollision
            CollisionStyle.Bounce -> ::applyBounceCollision
        }
        f.invoke(larger, smaller)
    }
    catch (e: IllegalStateException) {
        println("Error applying collision $collisionStyle: $e")
        null
    }
}

private fun applyBreakCollision(larger: Body, smaller: Body): CollisionResults? {
    return null
}

private fun applyBounceCollision(larger: Body, smaller: Body): CollisionResults? {
    return null
}

private fun applyMergeCollision(larger: Body, smaller: Body): CollisionResults? {
    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    val totalMass = larger.mass + smaller.mass

    val transferredMomentum = smaller.momentum * overlapAmount
    val transferredMass = transferredMomentum / smaller.velocity
    val remainingMass = smaller.mass - transferredMass

    // If remaining mass is very small, ignore this collision.
    return if (remainingMass > 1.kg) {
        val largeMomentum = larger.momentum + transferredMomentum

        larger.mass += transferredMass
        larger.velocity = largeMomentum / larger.mass
        smaller.mass = totalMass - larger.mass

        larger.radius = sizeOf(larger.mass)
        smaller.radius = sizeOf(smaller.mass)

        null
    } else {
        CollisionResults(removed = listOf(smaller))
    }
}


internal class CollisionResults(
    val added: List<Body> = listOf(),
    val removed: List<Body> = listOf()
) {
    operator fun component1() = added
    operator fun component2() = removed
}
