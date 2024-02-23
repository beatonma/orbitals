package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.overlapOf
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.getRadialPosition


/**
 * See [CollisionStyle.Bouncy].
 */
internal val BouncyCollision = Collision { larger, smaller, physics ->
    when {
        overlapOf(larger, smaller) > .5f ->
            // Many bodies can result in severe clipping.
            // If this occurs, allow the objects to merge together to avoid flickering.
            MergeCollision(larger, smaller, physics)

        else -> {
            applySimpleCollision(larger, smaller, CoefficientOfRestitution.Bouncy)
            null
        }
    }
}

/**
 * See [CollisionStyle.Sticky].
 */
internal val StickyCollision = Collision { larger, smaller, physics ->
    when {
        overlapOf(larger, smaller) > .5f ->
            // Many bodies can result in severe clipping.
            // If this occurs, allow the objects to merge together to avoid flickering.
            MergeCollision(larger, smaller, physics)

        else -> {
            applySimpleCollision(larger, smaller, CoefficientOfRestitution.Sticky)
            null
        }
    }
}

/**
 * Coefficient of Restitution: https://en.wikipedia.org/wiki/Coefficient_of_restitution
 */
private enum class CoefficientOfRestitution(val value: Float) {
    Inelastic(0f),
    Sticky(.2f),
    Bouncy(.9f),
    Elastic(1f),
}

/**
 * Apply a simple collision, updating the motion of each body without any other effects.
 */
private fun applySimpleCollision(
    larger: Body,
    smaller: Body,
    coefficientOfRestitution: CoefficientOfRestitution,
) {
    val (largerV, smallerV) = calculateCollisionVelocities(
        larger,
        smaller,
        coefficientOfRestitution
    )

    if (larger is Inertial) {
        larger.velocity = largerV
    }

    if (smaller is Inertial) {
        smaller.position = getRadialPosition(
            larger.position,
            larger.radius + smaller.radius,
            larger.position.angleTo(smaller.position)
        )
        smaller.velocity = smallerV
    }
}

private fun calculateCollisionVelocities(
    left: Body,
    right: Body,
    coefficient: CoefficientOfRestitution,
): Pair<Velocity, Velocity> {
    fun velocityAfter(a: Body, b: Body): Velocity {
        return (
                (a.momentum + b.momentum + (b.mass * coefficient.value * (b.velocity - a.velocity)))
                        / (a.mass + b.mass)
                )
    }

    return Pair(
        velocityAfter(left, right),
        velocityAfter(right, left)
    )
}
