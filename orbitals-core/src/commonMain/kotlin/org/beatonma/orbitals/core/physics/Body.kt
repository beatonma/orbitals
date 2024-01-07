package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.util.currentTimeMillis
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val CollisionMinimumAge = 1.seconds

val DefaultDensity = 0.5
val ZeroMass get() = 0.0.kg
val ZeroDistance get() = 0.0.metres
val ZeroPosition get() = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity get() = Velocity(0.0.metres.perSecond, 0.0.metres.perSecond)
val ZeroMotion get() = Motion(ZeroPosition, ZeroVelocity)
val ZeroAcceleration get() = Acceleration(AccelerationScalar(0f), AccelerationScalar(0f))


interface Fixed
interface Inertial
interface Collider {
    var lastCollision: Long
    fun canCollide(now: Long = currentTimeMillis()): Boolean
}

sealed interface Body : Collider {
    val id: UniqueID
    var mass: Mass
    var radius: Distance
    val motion: Motion

    var age: Duration

    var position: Position
        get() = motion.position
        set(value) {
            motion.position.x = value.x
            motion.position.y = value.y
        }
    var velocity: Velocity
        get() = motion.velocity
        set(value) {
            motion.velocity = value
        }

    var acceleration: Acceleration
        set(value) {
            motion.acceleration = value
        }
        get() = motion.acceleration

    val momentum: Momentum get() = mass * velocity

    override var lastCollision: Long
    override fun canCollide(now: Long): Boolean =
        now - lastCollision > CollisionMinimumAge.inWholeMilliseconds

    fun applyInertia(timeDelta: Duration)
    fun applyGravity(other: Body, timeDelta: Duration, G: Float)

    fun distanceTo(other: Body): Distance = position.distanceTo(other.position)

    fun tick(duration: Duration) {
        acceleration = ZeroAcceleration
        applyInertia(duration)
        age += duration
    }

    fun toSimpleString(): String =
        "${this::class.simpleName} $mass $radius ${velocity.magnitude}"
}


/**
 * A body that stays in a fixed position.
 */
data class FixedBody(
    override var mass: Mass,
    override val id: UniqueID = uniqueID("FixedBody"),
    override var radius: Distance = sizeOf(mass),
    override val motion: Motion = ZeroMotion,
    override var age: Duration = 0.seconds
) : Body, Fixed {
    override var lastCollision: Long = currentTimeMillis()

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

data class InertialBody(
    override val id: UniqueID = uniqueID("InertialBody"),
    override var mass: Mass,
    override var radius: Distance = sizeOf(mass),
    override val motion: Motion = ZeroMotion,
    override var age: Duration = 0.seconds
) : Body, Inertial {
    override var lastCollision: Long = currentTimeMillis()

    constructor(
        mass: Mass,
        id: UniqueID = uniqueID("InertialBody"),
        radius: Distance = sizeOf(mass),
        position: Position = ZeroPosition,
        velocity: Velocity = ZeroVelocity,
    ) : this(id, mass, radius, Motion(position, velocity))

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


data class GreatAttractor(
    override var mass: Mass,
    override val id: UniqueID = uniqueID("GreatAttractor"),
    override var radius: Distance = sizeOf(mass),
    override val motion: Motion = ZeroMotion,
    override var age: Duration = 0.seconds
) : Body, Fixed {
    override var lastCollision: Long = currentTimeMillis()

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


fun sizeOf(mass: Mass, density: Double = DefaultDensity): Distance {
    val volume = mass.value / density
    val radius = ((3.0 * volume) / 4.0 * kotlin.math.PI).pow(1.0 / 3.0)
    return radius.metres
}

fun Body.inContactWith(other: Body): Boolean {
    val distance = this.position.distanceTo(other.position)
    return distance < radius || distance < other.radius
}

fun centerOfMass(a: Body, b: Body): Position {
    val totalMass = a.mass + b.mass

    val centerOfMass =
        (
                (a.position.toGeneric() * a.mass.toGeneric())
                        + (b.position.toGeneric() * b.mass.toGeneric())
                ) * (1f / totalMass.value)

    return Position(centerOfMass.x.value, centerOfMass.y.value)
}
