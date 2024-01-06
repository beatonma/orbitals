package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Inertial
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.centerOfMass
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.physics.getRadialPosition
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.sizeOf
import org.beatonma.orbitals.core.physics.times
import org.beatonma.orbitals.core.util.currentTimeMillis
import org.beatonma.orbitals.core.util.warn
import kotlin.random.Random

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


internal fun applyCollision(
    a: Body,
    b: Body,
    collisionStyle: CollisionStyle
): CollisionResults? {
    if (collisionStyle == CollisionStyle.None) return null
    val now = currentTimeMillis()
    if (!a.canCollide(now) || !b.canCollide(now)) {
        return null
    }
    else {
        a.lastCollision = now
        b.lastCollision = now
    }

    val (larger, smaller) = arrayOf(a, b).apply { sortByDescending(Body::mass) }

    CollisionResults.clear()

    return try {
        val f = when (collisionStyle) {
            CollisionStyle.None -> return null
            CollisionStyle.Merge -> ::applyMergeCollision
            CollisionStyle.Break -> ::applyBreakCollision
            CollisionStyle.Bouncy -> ::applyBounceCollision
            CollisionStyle.Sticky -> ::applyStickyCollision
        }
        return f.invoke(larger, smaller)
    } catch (e: IllegalStateException) {
        warn("Error applying collision $collisionStyle: $e")
        null
    }
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

/**
 * See [CollisionStyle.Break].
 */
private fun applyBreakCollision(larger: Body, smaller: Body): CollisionResults? {
    if (smaller.mass < 5.kg) {
        return CollisionResults.removed(smaller)
    }

    val distance = larger.distanceTo(smaller)
    val overlapAmount = distance / (larger.radius + smaller.radius)
    if (overlapAmount <= 0f) return null

    val totalMass = larger.mass + smaller.mass
    val angle = larger.position.angleTo(smaller.position)
    val collisionPoint = larger.position + (angle * (distance - smaller.radius))

//    val directionSimilarity = dot(larger.velocity.direction, smaller.velocity.direction)
    val massRatio = smaller.mass / totalMass
    val ejectaMass = totalMass * overlapAmount
    val massFromSmaller = massRatio * ejectaMass
    val massFromLarger = ejectaMass - massFromSmaller

    larger.updateMassAndSize(larger.mass - massFromLarger)
    smaller.updateMassAndSize(smaller.mass - massFromSmaller)

    return CollisionResults.added(
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

        larger.updateMassAndSize(larger.mass + transferredMass)
        smaller.updateMassAndSize(totalMass - larger.mass)

        larger.velocity = largeMomentum / larger.mass

        null
    }
}

private fun Body.updateMassAndSize(mass: Mass) {
    this.mass = mass
    this.radius = sizeOf(mass)
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


fun Body.explode(ejectaCount: Int = Random.nextInt(5, 20)): List<Body> {
    val ejectaMass = mass.value.divideUnevenly(ejectaCount)
        .map(::Mass)

    val ejectaMomentum = momentum.divideUnevenly(ejectaCount)

    return ejectaMass.zip(ejectaMomentum) { mass, momentum ->
        val velocity = momentum / mass

        InertialBody(
            mass = mass,
            motion = Motion(
                position,
                velocity,
            )
        )
    }
}
