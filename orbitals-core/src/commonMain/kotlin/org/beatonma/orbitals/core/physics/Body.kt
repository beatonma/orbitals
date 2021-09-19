package org.beatonma.orbitals.core.physics

import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val ZeroMass get() = 0.0.kg
val ZeroDistance get() = 0.0.metres
val ZeroPosition get() = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity get() = Velocity(0.0.metres.perSecond, 0.0.metres.perSecond)
val ZeroMotion get() = Motion(ZeroPosition, ZeroVelocity)
val ZeroAcceleration get() = Acceleration(AccelerationScalar(0f), AccelerationScalar(0f))


@OptIn(ExperimentalTime::class)
interface Senescent {
    var age: Duration
}

interface Fixed
interface Inertial

@OptIn(ExperimentalTime::class)
sealed interface Body {
    val id: UniqueID
    val mass: Mass
    val radius: Distance
    val motion: Motion

    var position: Position
        get() = motion.position
        set(value) {
            motion.position.x = value.x
            motion.position.y = value.y
        }
    val velocity: Velocity
        get() = motion.velocity

    var acceleration: Acceleration
        set(value) {
            motion.acceleration = value
        }
        get() = motion.acceleration

    val diameter: Distance get() = radius * 2

    fun applyInertia(timeDelta: Duration)
    fun applyGravity(other: Body, timeDelta: Duration, G: Float)

    fun distanceTo(other: Body): Distance = position.distanceTo(other.position)

    fun tick(duration: Duration) {
        applyInertia(duration)
    }
}


/**
 * A body that stays in a fixed position.
 */
@OptIn(ExperimentalTime::class)
data class FixedBody(
    override val id: UniqueID = uniqueID("FixedBody"),
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val motion: Motion = ZeroMotion,
    override var age: Duration = Duration.seconds(0)
) : Body, Fixed, Senescent {

    override fun applyInertia(timeDelta: Duration) {
        // N/A
    }

    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {
        // N/A
    }

    override fun tick(duration: Duration) {
        super.tick(duration)
        age += duration
    }
}

fun FixedBody.toInertialBody() = InertialBody(
    id = id,
    mass = mass,
    radius = radius,
    motion = motion,
)

@OptIn(ExperimentalTime::class)
data class InertialBody(
    override val id: UniqueID = uniqueID("InertialBody"),
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val motion: Motion = ZeroMotion,
) : Body, Inertial {

    override fun applyInertia(timeDelta: Duration) {
        motion.applyInertia(timeDelta)
    }

    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {
        val theta: Angle = position.angleTo(other.position)
        val force: Force = calculateForce(other, G)
        val acceleration: Acceleration = calculateAcceleration(force, theta)

        velocity += (acceleration * timeDelta)
        this.acceleration += acceleration
    }

    internal fun calculateForce(other: Body, G: Float): Force =
        calculateGravitationalForce(this.mass, other.mass, distanceTo(other), G = G)

    /**
     * Calculate acceleration due to gravity.
     */
    internal fun calculateAcceleration(force: Force, angle: Angle): Acceleration =
        Acceleration(force / mass, angle)

}


@OptIn(ExperimentalTime::class)
data class GreatAttractor(
    override val id: UniqueID = uniqueID("GreatAttractor"),
    override val mass: Mass,
    override val radius: Distance = ZeroDistance,
    override val motion: Motion = ZeroMotion,
    override var age: Duration = Duration.seconds(0)
) : Body, Fixed, Senescent {

    override fun applyInertia(timeDelta: Duration) {
        // N/A
    }

    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {
        // N/A
    }

    override fun tick(duration: Duration) {
        super.tick(duration)
        age += duration
    }
}


fun uniqueID(name: Any): UniqueID = UniqueID("$name[$uniqueID]")
private val uniqueID: String get() = UUID.randomUUID().toString().substring(0, 5)

value class UniqueID internal constructor(val value: String) {
    override fun toString(): String {
        return "id:$value"
    }
}
