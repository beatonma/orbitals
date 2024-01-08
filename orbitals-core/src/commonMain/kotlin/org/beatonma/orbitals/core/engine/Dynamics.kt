package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.engine.collision.BouncyCollision
import org.beatonma.orbitals.core.engine.collision.BreakCollision
import org.beatonma.orbitals.core.engine.collision.CollisionLog
import org.beatonma.orbitals.core.engine.collision.CollisionResults
import org.beatonma.orbitals.core.engine.collision.MergeCollision
import org.beatonma.orbitals.core.engine.collision.StickyCollision
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.divideUnevenly
import org.beatonma.orbitals.core.util.currentTimeMillis
import org.beatonma.orbitals.core.util.warn
import kotlin.random.Random


internal fun applyCollision(
    larger: Body,
    smaller: Body,
    collisionStyle: CollisionStyle,
    reporter: CollisionLog = CollisionResultsImpl,
): CollisionResults? {
    if (collisionStyle == CollisionStyle.None) return null

    require(larger.mass >= smaller.mass) { "Expected objects to be given in descending order of mass." }

    val now = currentTimeMillis()
    if (!larger.canCollide(now) || !smaller.canCollide(now)) {
        return null
    } else {
        larger.lastCollision = now
        smaller.lastCollision = now
    }

    reporter.clear()

    try {
        val collision = when (collisionStyle) {
            CollisionStyle.None -> return null
            CollisionStyle.Merge -> MergeCollision
            CollisionStyle.Break -> BreakCollision
            CollisionStyle.Bouncy -> BouncyCollision
            CollisionStyle.Sticky -> StickyCollision
        }
        return collision.invoke(larger, smaller, reporter)
    } catch (e: IllegalStateException) {
        warn("Error applying collision $collisionStyle (${larger.toSimpleString()} vs ${smaller.toSimpleString()}): $e")
        throw e
    }
}


private object CollisionResultsImpl : CollisionResults, CollisionLog {
    override val added: MutableList<Body> = mutableListOf()
    override val removed: MutableList<Body> = mutableListOf()

    override fun add(body: Body) = apply {
        added.add(body)
    }

    override fun remove(body: Body) = apply {
        removed.add(body)
    }

    override fun add(bodies: List<Body>) = apply {
        added.addAll(bodies)
    }

    override fun remove(bodies: List<Body>) = apply {
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
            motion = Motion(
                position,
                velocity,
            )
        )
    }
}
