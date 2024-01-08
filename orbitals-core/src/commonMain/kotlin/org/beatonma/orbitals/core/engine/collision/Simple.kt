package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.centerOfMass
import org.beatonma.orbitals.core.physics.getRadialPosition


/**
 * See [CollisionStyle.Bouncy].
 */
internal val BouncyCollision = Collision { larger, smaller, _ ->
    applySimpleCollision(larger, smaller, CoR.Bouncy)
    null
}

/**
 * See [CollisionStyle.Sticky].
 */
internal val StickyCollision = Collision { larger, smaller, _ ->
    applySimpleCollision(larger, smaller, CoR.Sticky)
    null
}

/**
 * Coefficient of Restitution: https://en.wikipedia.org/wiki/Coefficient_of_restitution
 */
private enum class CoR(val value: Float) {
    Default(0.1f),
    Inelastic(0f),
    Sticky(.2f),
    Bouncy(.8f),
    Elastic(1f),
}

/**
 * Apply a simple collision, updating the motion of each body without any other effects.
 */
private fun applySimpleCollision(
    larger: Body,
    smaller: Body,
    coefficientOfRestitution: CoR,
) {
    val center = centerOfMass(larger, smaller)
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
