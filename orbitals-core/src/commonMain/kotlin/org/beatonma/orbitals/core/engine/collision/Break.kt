package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.collision.Collision.Companion.updateMassAndSize
import org.beatonma.orbitals.core.engine.explode
import org.beatonma.orbitals.core.engine.overlapOf
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Angle
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.degrees
import org.beatonma.orbitals.core.physics.rangeTo
import org.beatonma.orbitals.core.physics.times


// Ejecta will be directed in a cone of this size, centered around the normal.
private val EjectaAngle = 120f.degrees

private enum class Direction(val angle: Angle) {
    Forward(0f.degrees),
    Backward(180f.degrees),
    ;
}

private fun ejectaAngleRange(direction: Direction) =
    (direction.angle - (EjectaAngle / 2))..(direction.angle + (EjectaAngle / 2))

/**
 * See [CollisionStyle.Break].
 */
internal val BreakCollision = Collision { larger, smaller, changes ->
    if (smaller.mass < Config.MinObjectMass) {
        return@Collision changes.remove(smaller.id)
    }

    val overlapAmount = overlapOf(larger, smaller)
    if (overlapAmount > .75f) return@Collision MergeCollision(larger, smaller, changes)

    val massRatio = larger.mass / smaller.mass
    if (massRatio in 0.8f..1.2f) {
        // Similarly sized objects -> destroy both!
        return@Collision changes + larger.explode() + smaller.explode() - larger.id - smaller.id
    }

    val angle = smaller.position.angleTo(larger.position)
    val distance = larger.distanceTo(smaller)
    val collisionPoint = smaller.position + (angle * (distance - larger.radius))

    val ejectaMass = overlapAmount * smaller.mass
    val ejectaMomentum = ejectaMass * smaller.velocity
    val direction = when {
        massRatio > 2f -> Direction.Backward
        else -> Direction.Forward
    }
    val ejecta =
        explode(
            collisionPoint,
            ejectaMass,
            smaller.density,
            ejectaMomentum,
            ejectaAngleRange(direction),
        )

    when {
        ejecta.isEmpty() -> null
        else -> {
            smaller.updateMassAndSize(smaller.mass - ejectaMass)
            changes + ejecta
        }
    }
}
