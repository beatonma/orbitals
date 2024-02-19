package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.engine.collision.BouncyCollision
import org.beatonma.orbitals.core.engine.collision.BreakCollision
import org.beatonma.orbitals.core.engine.collision.Collision
import org.beatonma.orbitals.core.engine.collision.CollisionLog
import org.beatonma.orbitals.core.engine.collision.CollisionResults
import org.beatonma.orbitals.core.engine.collision.MergeCollision
import org.beatonma.orbitals.core.engine.collision.StickyCollision
import org.beatonma.orbitals.core.nextFloat
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Angle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Momentum
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Speed
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.degrees
import org.beatonma.orbitals.core.physics.distanceTo
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.physics.inContactWith
import org.beatonma.orbitals.core.physics.metresPerSecond
import org.beatonma.orbitals.core.physics.rangeTo
import org.beatonma.orbitals.core.physics.rawDegrees
import org.beatonma.orbitals.core.physics.rotateBy
import org.beatonma.orbitals.core.util.warn
import kotlin.random.Random


internal fun applyCollision(
    a: Body,
    b: Body,
    collisionStyle: CollisionStyle,
    reporter: CollisionLog = CollisionResultsImpl,
): CollisionResults? {
    if (collisionStyle == CollisionStyle.None) return null
    if (!a.inContactWith(b)) return null

    if (!a.canCollide() || !b.canCollide()) {
        return null
    }

    val (larger, smaller) = arrayOf(a, b).sortedByDescending(Body::mass)
    try {
        reporter.clear()
        val collision: Collision = when (collisionStyle) {
            CollisionStyle.None -> return null
            CollisionStyle.Merge -> MergeCollision
            CollisionStyle.Break -> BreakCollision
            CollisionStyle.Bouncy -> BouncyCollision
            CollisionStyle.Sticky -> StickyCollision
        }
        return collision(larger, smaller, reporter)
    } catch (e: IllegalStateException) {
        warn("Error applying collision $collisionStyle (${larger.toSimpleString()} vs ${smaller.toSimpleString()}): $e")
        throw e
    }
}


private object CollisionResultsImpl : CollisionResults, CollisionLog {
    override val added: MutableList<Body> = mutableListOf()
    override val removed: MutableList<UniqueID> = mutableListOf()

    override fun add(body: Body) = apply {
        added.add(body)
    }

    override fun remove(body: UniqueID) = apply {
        removed.add(body)
    }

    override fun add(bodies: List<Body>) = apply {
        added.addAll(bodies)
    }

    override fun remove(bodies: List<UniqueID>) = apply {
        removed.addAll(bodies)
    }

    override fun clear() {
        added.clear()
        removed.clear()
    }
}


fun explode(
    position: Position,
    totalMass: Mass,
    density: Density,
    totalMomentum: Momentum,
    angleRange: ClosedFloatingPointRange<Angle> = 0f.degrees..360f.rawDegrees,
): List<Body> {
    val minEjectaSpeed = 500f.metresPerSecond//getEscapeSpeed(totalMass, ZeroDistance, G)
    val ejectaCount = Random.nextInt(10, 30)

    // Artificially reduce mass so we can dump more momentum into velocity.
    val mass = totalMass / (ejectaCount * 2.5f)
    if (mass < Config.MinObjectMass) return emptyList()

    return totalMomentum.divideUnevenly(ejectaCount).map { momentum ->
        InertialBody(
            mass,
            density,
            motion = Motion(
                position = position,
                velocity = (momentum / mass).coerceMinSpeed(minEjectaSpeed).rotateBy(
                    Random.nextFloat(
                        angleRange.start.asDegrees,
                        angleRange.endInclusive.asDegrees
                    ).degrees
                )
            )
        )
    }
}

fun Body.explode(): List<Body> = explode(position, mass, density, momentum)
private fun Velocity.coerceMinSpeed(minSpeed: Speed): Velocity {
    if (magnitude < minSpeed) {
        return Velocity(minSpeed, angle)
    }
    return this
}

/**
 * Returns a 0..1 score of how much two bodies are overlapping each other.
 */
internal fun overlapOf(a: Body, b: Body): Float {
    val distance = a.position.distanceTo(b.position)

    val minDiameter = minOf(a.radius, b.radius) * 2
    val radiiSum = a.radius + b.radius

    return ((radiiSum - distance) / minDiameter).coerceIn(0f, 1f)
}
