package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.engine.CollisionMinimumAge
import org.beatonma.orbitals.core.util.currentTimeMillis
import kotlin.math.PI
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


val ZeroMass = 0f.kg
val ZeroDistance = 0f.metres
val ZeroAcceleration = Acceleration(AccelerationScalar(0f), AccelerationScalar(0f))
val ZeroPosition = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity = Velocity(0f.metres.perSecond, 0f.metres.perSecond)
val ZeroMotion get() = Motion(ZeroPosition, ZeroVelocity)


interface Fixed
interface Inertial
interface Collider {
    var lastCollision: Long
    fun canCollide(now: Long = currentTimeMillis()): Boolean
}

sealed interface Body : Collider {
    val id: UniqueID
    val density: Density
    var mass: Mass
    var radius: Distance
    val motion: Motion

    var age: Duration

    var position: Position
        get() = motion.position
        set(value) {
            motion.position = value
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
    override fun canCollide(now: Long): Boolean = mass != ZeroMass &&
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

    /**
     * .equals, but ignore `id` field.
     */
    fun physicsEquals(other: Body): Boolean =
        density == other.density
                && mass == other.mass
                && radius == other.radius
                && motion == other.motion
                && age == other.age
}


/**
 * A body that stays in a fixed position.
 */
data class FixedBody(
    override var mass: Mass,
    override var density: Density,
    override val motion: Motion = ZeroMotion,
    override var radius: Distance = sizeOf(mass, density),
    override val id: UniqueID = uniqueID("FixedBody"),
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
    density = density,
    mass = mass,
    radius = radius,
    motion = motion,
    age = age,
)

data class InertialBody(
    override var mass: Mass,
    override val density: Density,
    override val motion: Motion = ZeroMotion,
    override var radius: Distance = sizeOf(mass, density),
    override var age: Duration = 0.seconds,
    override val id: UniqueID = uniqueID("InertialBody"),
) : Body, Inertial {
    override var lastCollision: Long = currentTimeMillis()

    override fun applyInertia(timeDelta: Duration) {
        motion.applyInertia(timeDelta)
    }

    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {
        if (mass == ZeroMass || other.mass == ZeroMass) return

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
    override val density: Density,
    override val motion: Motion = ZeroMotion,
    override var radius: Distance = sizeOf(mass, density),
    override val id: UniqueID = uniqueID("GreatAttractor"),
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


fun sizeOf(mass: Mass, density: Density): Distance {
    val volume = mass / density
    val radius = ((volume * 3f).value.toDouble() / (4.0 * PI)).pow(1.0 / 3.0)
    return radius.metres
}

fun Body.inContactWith(other: Body): Boolean =
    position.distanceTo(other.position) <= (radius + other.radius)

fun centerOfMass(a: Body, b: Body): Position {
    val totalMass = a.mass + b.mass

    val centerOfMass =
        (
                (a.position.toGeneric() * a.mass.toGeneric())
                        + (b.position.toGeneric() * b.mass.toGeneric())
                ) * (1f / totalMass.value)

    return Position(centerOfMass.x.value, centerOfMass.y.value)
}
