package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.util.currentTimeMillis
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val CollisionMinimumAge = 1.seconds

val ZeroMass = 0.0.kg
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
    override var density: Density,
    override var mass: Mass,
    override val id: UniqueID = uniqueID("FixedBody"),
    override var radius: Distance = sizeOf(mass, density),
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

    override fun equals(other: Any?): Boolean = when (other) {
        is Body -> this.id == other.id
        else -> false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun FixedBody.toInertialBody() = InertialBody(
    id = id,
    density = density,
    mass = mass,
    radius = radius,
    motion = motion,
)

data class InertialBody(
    override val id: UniqueID = uniqueID("InertialBody"),
    override var mass: Mass,
    override val density: Density,
    override var radius: Distance = sizeOf(mass, density),
    override val motion: Motion = ZeroMotion,
    override var age: Duration = 0.seconds
) : Body, Inertial {
    override var lastCollision: Long = currentTimeMillis()

    constructor(
        mass: Mass,
        density: Density,
        id: UniqueID = uniqueID("InertialBody"),
        radius: Distance = sizeOf(mass, density),
        position: Position = ZeroPosition,
        velocity: Velocity = ZeroVelocity,
    ) : this(id, mass, density, radius, Motion(position, velocity))

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

    override fun equals(other: Any?): Boolean = when (other) {
        is Body -> this.id == other.id
        else -> false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


data class GreatAttractor(
    override var mass: Mass,
    override val density: Density,
    override val id: UniqueID = uniqueID("GreatAttractor"),
    override var radius: Distance = sizeOf(mass, density),
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

    override fun equals(other: Any?): Boolean = when (other) {
        is Body -> this.id == other.id
        else -> false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


fun sizeOf(mass: Mass, density: Density): Distance {
    val volume = mass / density
    val radius = ((volume * 3f / 4f).value.toDouble() * kotlin.math.PI).pow(1.0 / 3.0)
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
