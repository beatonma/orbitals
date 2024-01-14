package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.engine.collision.BouncyCollision
import org.beatonma.orbitals.core.engine.collision.BreakCollision
import org.beatonma.orbitals.core.engine.collision.Collision
import org.beatonma.orbitals.core.engine.collision.CollisionLog
import org.beatonma.orbitals.core.engine.collision.CollisionResults
import org.beatonma.orbitals.core.engine.collision.MergeCollision
import org.beatonma.orbitals.core.engine.collision.StickyCollision
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.distanceTo
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.physics.inContactWith
import org.beatonma.orbitals.core.util.currentTimeMillis
import org.beatonma.orbitals.core.util.warn
import kotlin.random.Random


internal fun applyCollision(
    a: Body,
    b: Body,
    collisionStyle: CollisionStyle,
    reporter: CollisionLog = CollisionResultsImpl,
    now: Long = currentTimeMillis(),
): CollisionResults? {
    if (!a.inContactWith(b)) return null
    if (collisionStyle == CollisionStyle.None) return null

    if (!a.canCollide(now) || !b.canCollide(now)) {
        return null
    } else {
        a.lastCollision = now
        b.lastCollision = now
    }

    val (larger, smaller) = arrayOf(a, b).sortedByDescending { it.mass }
    require(larger.mass >= smaller.mass) {
        "Mass order wrong?! ${larger.mass} vs ${smaller.mass}"
    }

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


fun Body.explode(ejectaCount: Int = Random.nextInt(5, 20)): List<Body> {
    val ejectaMass = mass.value.divideUnevenly(ejectaCount)
        .map(::Mass)

    val ejectaMomentum = momentum.divideUnevenly(ejectaCount)

    return ejectaMass.zip(ejectaMomentum) { mass, momentum ->
        val velocity = momentum / mass

        InertialBody(
            mass = mass,
            density = density,
            motion = Motion(
                position,
                velocity,
            )
        )
    }
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
