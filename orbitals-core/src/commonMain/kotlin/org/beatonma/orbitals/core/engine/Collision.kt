package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.centerOf
import org.beatonma.orbitals.core.physics.getRadialPosition
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.sizeOf
import org.beatonma.orbitals.core.util.warn

/**
 * Coefficient of Restitution: https://en.wikipedia.org/wiki/Coefficient_of_restitution
 * 1 -> elastic collision
 * 0 -> inelastic collision
 */
private enum class CoR(val value: Float) {
    Default(0.1f),
    Inelastic(0f),
    Sticky(.2f),
    Bouncy(.8f),
    Elastic(1f),
}


internal fun applyCollision(
    larger: Body,
    smaller: Body,
    collisionStyle: CollisionStyle
): CollisionResults? {
    check(larger.mass >= smaller.mass) {
        "applyCollision expects bodies to be ordered by descending mass!"
    }

    CollisionResults.clear()

    return try {
        val f = when (collisionStyle) {
            CollisionStyle.None -> return null
            CollisionStyle.Merge -> ::applyMergeCollision
            CollisionStyle.Break -> ::applyBreakCollision
            CollisionStyle.Bouncy -> ::applyBounceCollision
            CollisionStyle.Sticky -> ::applyStickyCollision
        }
        f.invoke(larger, smaller)
    } catch (e: IllegalStateException) {
        warn("Error applying collision $collisionStyle: $e")
        null
    }
}

/**
 * See [CollisionStyle.Break].
 */
private fun applyBreakCollision(larger: Body, smaller: Body): CollisionResults? {
    return null
}

/**
 * See [CollisionStyle.Bouncy].
 */
private fun applyBounceCollision(larger: Body, smaller: Body): CollisionResults? {
    applySimpleCollision(larger, smaller, CoR.Bouncy)
    return null
}

/**
 * See [CollisionStyle.Sticky].
 */
private fun applyStickyCollision(larger: Body, smaller: Body): CollisionResults? {
    applySimpleCollision(larger, smaller, CoR.Sticky)
    return null
}

/**
 * Apply a simple collision, updating the motion of each body without any other effects.
 */
private fun applySimpleCollision(
    larger: Body,
    smaller: Body,
    coefficientOfRestitution: CoR,
) {
    val center = centerOf(larger.position, smaller.position)
    val (largerV, smallerV) = calculateCollisionVelocities(
        larger,
        smaller,
        coefficientOfRestitution
    )

    if (larger is Inertial) {
        larger.position = getRadialPosition(center, larger.radius, center.angleTo(larger.position))
        larger.velocity = largerV
    }

    if (smaller is Inertial) {
        smaller.position =
            getRadialPosition(center, smaller.radius, center.angleTo(smaller.position))
        smaller.velocity = smallerV
    }
}

/**
 * See [CollisionStyle.Merge].
 */
private fun applyMergeCollision(larger: Body, smaller: Body): CollisionResults? {
    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    val totalMass = larger.mass + smaller.mass

    val transferredMomentum = smaller.momentum * overlapAmount
    val transferredMass = transferredMomentum / smaller.velocity
    val remainingMass = smaller.mass - transferredMass

    return if (remainingMass <= 1.kg) {
        // If remaining mass is very small, destroy the object.
        CollisionResults.removed(smaller)
    } else {
        val largeMomentum = larger.momentum + transferredMomentum

        larger.mass += transferredMass
        larger.velocity = largeMomentum / larger.mass
        smaller.mass = totalMass - larger.mass

        larger.radius = sizeOf(larger.mass)
        smaller.radius = sizeOf(smaller.mass)

        null
    }
}


internal object CollisionResults {
    val added: MutableList<Body> = mutableListOf()
    val removed: MutableList<Body> = mutableListOf()

    operator fun component1() = added
    operator fun component2() = removed

    fun added(body: Body) = apply {
        added.add(body)
    }

    fun removed(body: Body) = apply {
        removed.add(body)
    }

    fun added(bodies: List<Body>) = apply {
        added.addAll(bodies)
    }

    fun removed(bodies: List<Body>) = apply {
        removed.addAll(bodies)
    }

    fun clear() {
        added.clear()
        removed.clear()
    }
}


private fun calculateCollisionVelocities(
    left: Body,
    right: Body,
    coefficientOfRestitution: CoR = CoR.Default,
) = calculateCollisionVelocities(left, right, coefficientOfRestitution.value)


private fun calculateCollisionVelocities(
    left: Body,
    right: Body,
    coefficientOfRestitution: Float,
): Pair<Velocity, Velocity> {
    val totalMomentum = left.momentum + right.momentum
    val totalMass = left.mass + right.mass

    fun common(a: Body, b: Body): Velocity {
        return (b.velocity - a.velocity) * coefficientOfRestitution * b.mass.value
    }

    fun velocity(body: Body, other: Body): Velocity {
        val (vX, vY) = common(body, other)

        return Velocity(
            (vX.value + totalMomentum.x.value) / totalMass.value,
            (vY.value + totalMomentum.y.value) / totalMass.value,
        )
    }

    return Pair(
        velocity(left, right),
        velocity(right, left)
    )
}
