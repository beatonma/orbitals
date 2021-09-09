package org.beatonma.orbitals.physics

import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val ZeroMass = 0.0.kg
val ZeroDistance = 0.0.metres
val ZeroPosition = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity = Velocity(0.0.metres.perSecond, 0.0.metres.perSecond)
val ZeroMotion = Motion(ZeroPosition, ZeroVelocity)
val ZeroAcceleration = Acceleration(AccelerationScalar(0f), AccelerationScalar(0f), AccelerationScalar(0f))

@OptIn(ExperimentalTime::class)
interface Body {
    val id: UniqueID
    val mass: Mass
    val radius: Distance
    val motion: Motion

    val position: Position get() = motion.position
    val velocity: Velocity get() = motion.velocity
    var acceleration: Acceleration
        set(value) {
            motion.acceleration = value
        }
        get() = motion.acceleration

    val diameter: Distance get() = radius * 2

    fun applyInertia(timeDelta: Duration)
    fun applyGravity(other: Body, timeDelta: Duration)

    fun distanceTo(other: Body): Distance = position.distanceTo(other.position)
}

/**
 * A body that stays in a fixed position.
 */
@OptIn(ExperimentalTime::class)
data class FixedBody(
    override val id: UniqueID = uniqueID("FixedBody"),
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val position: Position,
) : Body {
    override val motion: Motion = ZeroMotion
    override fun applyInertia(timeDelta: Duration) {
        // N/A
    }

    override fun applyGravity(other: Body, timeDelta: Duration) {
        // N/A
    }
}

@OptIn(ExperimentalTime::class)
data class InertialBody(
    override val id: UniqueID = uniqueID("InertialBody"),
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val motion: Motion = ZeroMotion,
) : Body {
    override fun applyInertia(timeDelta: Duration) {
        motion.applyInertia(timeDelta)
    }

    override fun applyGravity(other: Body, timeDelta: Duration) {
        val theta = position.angleTo(other.position)
        val force: Force = calculateForce(other)
        val acceleration = calculateAcceleration(force, theta)

        velocity += (acceleration * timeDelta)
    }

    private fun calculateForce(other: Body): Force {
        check(this.position != other.position) {
            "Same position ${this.id} vs ${other.id}"
        }

        return calculateGravitationalForce(this.mass, other.mass, distanceTo(other))
    }

    /**
     * Calculate acceleration due to gravity.
     */
    private fun calculateAcceleration(force: Force, angle: Angle): Acceleration {
        return Acceleration(force / mass, angle)
    }
}


fun uniqueID(name: Any): UniqueID = UniqueID("$name[$uniqueID]")
private val uniqueID: String get() = UUID.randomUUID().toString().substring(0, 5)

@JvmInline
value class UniqueID(val value: String)
