package org.beatonma.orbitalslivewallpaper.orbitals.physics

import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val ZeroMass = 0.0.kg
val ZeroDistance = 0.0.metres
val ZeroPosition = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity = Velocity(0.0.metres.perSecond, 0.0.metres.perSecond)
val ZeroAcceleration = Acceleration(0f)
val ZeroMotion = Motion(ZeroPosition, ZeroVelocity)
val ZeroBody = FixedBody(mass = ZeroMass, label = "ZERO_BODY", position = ZeroPosition)

@OptIn(ExperimentalTime::class)
interface Body {
    val label: Any
    val mass: Mass
    val radius: Distance
    val motion: Motion

    val position: Position get() = motion.position
    val velocity: Velocity get() = motion.velocity
    val acceleration: AccelerationDelta? get() = motion.accelerationDelta

    val diameter: Distance get() = radius * 2

    fun applyInertia(timeDelta: Duration)
    fun applyGravity(other: Body)

    fun distanceTo(other: Body): Distance = position.distanceTo(other.position)
}

/**
 * A body that stays in a fixed position.
 */
@OptIn(ExperimentalTime::class)
data class FixedBody(
    override val label: Any = "FixedBody",
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val position: Position,
) : Body {
    override val motion: Motion = ZeroMotion
    override fun applyInertia(timeDelta: Duration) {
        // N/A
    }

    override fun applyGravity(other: Body) {
        // N/A
    }
}

@OptIn(ExperimentalTime::class)
data class InertialBody(
    override val label: Any = "InertialBody",
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val motion: Motion = ZeroMotion,
) : Body {
    override fun applyInertia(timeDelta: Duration) {
        motion.applyInertia(timeDelta)
    }

    override fun applyGravity(other: Body) {
        val force: Force = calculateForce(other)
        val acceleration: Acceleration = calculateAcceleration(force)

        val theta = position.angleTo(other.position).asRadians

        val deltaX = cos(theta) * acceleration.value
        val deltaY = sin(theta) * acceleration.value

        motion.accelerationDelta = AccelerationDelta(acceleration.value, deltaX, deltaY)

        velocity.x += deltaX
        velocity.y += deltaY
    }

    private fun calculateForce(other: Body): Force {
        check(this.position != other.position) {
            "Same position ${this.label} vs ${other.label}"
        }

        return calculateGravitationalForce(this.mass, other.mass, distanceTo(other))
    }

    /**
     * Calculate acceleration due to gravity.
     */
    private fun calculateAcceleration(force: Force): Acceleration {
        return force / mass
    }
}
