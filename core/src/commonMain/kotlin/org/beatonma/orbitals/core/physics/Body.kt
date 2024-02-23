package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.engine.Config
import kotlin.math.PI
import kotlin.math.pow
import kotlin.time.Duration


val ZeroMass = 0f.kg
val ZeroDistance = 0f.metres
val ZeroAcceleration = Acceleration(AccelerationScalar(0f), AccelerationScalar(0f))
val ZeroPosition = Position(ZeroDistance, ZeroDistance)
val ZeroVelocity = Velocity(0f.metresPerSecond, 0f.metresPerSecond)
val ZeroMotion get() = Motion(ZeroPosition, ZeroVelocity)


enum class BodyState {
    /** Body is newly spawned and younger than [Config.CollisionMinimumAge] */
    New,

    /** Body is in its 'normal' state. */
    MainSequence,

    /** Body has grown too large (exceeding [Config.MaxObjectMass]) and is collapsing to a supernova. */
    Collapsing,

    /** Body is 'dead' but kept as an anchor for death animations. */
    Supernova,

    /** Body is ready for removal from the simulation. */
    Dead,
    ;
}

sealed interface Fixed : Body {
    override fun applyInertia(timeDelta: Duration) {}
    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {}
}

sealed interface Inertial : Body {
    override fun applyInertia(timeDelta: Duration) = motion.applyInertia(timeDelta)

    override fun applyGravity(other: Body, timeDelta: Duration, G: Float) {
        if (mass == ZeroMass || other.mass == ZeroMass) return
        if (state >= BodyState.Supernova) return
        if (age < Config.CollisionMinimumAge) return

        val theta: Angle = position.angleTo(other.position)
        val force: Force = calculateForce(other, G)
        val acceleration: Acceleration = calculateAcceleration(force, theta)

        velocity += (acceleration * timeDelta)
        this.acceleration += acceleration
    }

    fun calculateForce(other: Body, G: Float): Force =
        calculateGravitationalForce(this.mass, other.mass, distanceTo(other), G = G)

    /**
     * Calculate acceleration due to gravity.
     */
    fun calculateAcceleration(force: Force, angle: Angle): Acceleration =
        Acceleration(force / mass, angle)
}


sealed interface Body {
    val id: UniqueID
    val density: Density
    var mass: Mass
    var radius: Distance
    val motion: Motion
    var state: BodyState

    fun updateState(state: BodyState) {
        if (this.state >= state) return
        this.state = state
        sinceStateChange = Duration.ZERO

        when (state) {
            BodyState.Supernova -> {
                motion.velocity /= 2f
                mass = Config.MinObjectMass
            }

            BodyState.Dead -> {
                mass = ZeroMass
            }

            else -> {}
        }
    }

    fun stateEvent(): BodyState? = when (sinceStateChange) {
        Duration.ZERO -> state
        else -> null
    }

    /**
     * Time since this body was created.
     */
    var age: Duration

    /**
     * Time since this body changed to a different [BodyState].
     */
    var sinceStateChange: Duration
    var isMortal: Boolean
    val isImmortal: Boolean get() = !isMortal

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

    fun applyInertia(timeDelta: Duration)
    fun applyGravity(other: Body, timeDelta: Duration, G: Float)

    fun canCollide(): Boolean = mass != ZeroMass && state == BodyState.MainSequence

    fun distanceTo(other: Body): Distance = position.distanceTo(other.position)

    fun tick(duration: Duration) {
        acceleration = ZeroAcceleration
        applyInertia(duration)
        age += duration
        sinceStateChange += duration

        when (state) {
            BodyState.New -> {
                if (age > Config.CollisionMinimumAge) {
                    updateState(BodyState.MainSequence)
                }
            }

            BodyState.Collapsing -> {
                if (sinceStateChange > Config.CollapseDuration) {
                    updateState(BodyState.Supernova)
                }
            }

            BodyState.Supernova -> {
                if (sinceStateChange > Config.SupernovaDuration) {
                    updateState(BodyState.Dead)
                }
            }

            else -> {}
        }
    }

    fun collapse() {
        if (state == BodyState.MainSequence) {
            updateState(BodyState.Collapsing)
        }
    }

    fun isCollapsing() = this.state == BodyState.Collapsing
    fun isSupernova() = this.state == BodyState.Supernova
    fun isDead() = this.state == BodyState.Dead

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


data class InertialBody(
    override var mass: Mass,
    override val density: Density,
    override val motion: Motion = ZeroMotion,
    override var radius: Distance = sizeOf(mass, density),
    override var age: Duration = Duration.ZERO,
    override val id: UniqueID = uniqueID("InertialBody"),
    override var state: BodyState = BodyState.New,
) : Inertial {
    override var isMortal: Boolean = true
    override var sinceStateChange: Duration = Duration.ZERO
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
    override var age: Duration = Duration.ZERO,
    override var state: BodyState = BodyState.New,
) : Fixed {
    override var isMortal: Boolean = true
    override var sinceStateChange: Duration = Duration.ZERO

    init {
        motion.velocity = ZeroVelocity
    }
}


data class GreatAttractor(
    override var mass: Mass,
    override val density: Density,
    override val motion: Motion = ZeroMotion,
    override var radius: Distance = sizeOf(mass, density),
    override val id: UniqueID = uniqueID("GreatAttractor"),
    override var age: Duration = Duration.ZERO,
    override var state: BodyState = BodyState.New,
) : Fixed {
    override var isMortal: Boolean = true
    override var sinceStateChange: Duration = Duration.ZERO

    init {
        motion.velocity = ZeroVelocity
    }
}


fun Body.toInertialBody() = InertialBody(
    id = id,
    density = density,
    mass = mass,
    radius = radius,
    motion = motion.apply { velocity = ZeroVelocity },
    age = age,
)


fun sizeOf(mass: Mass, density: Density): Distance {
    val volume = mass / density

    // Tweaked volume-of-a-sphere function for a steeper increase in volume -> radius.
    return ((volume.value * 3f) / (4.0f * PI.toFloat())).pow(0.5f).metres
}

fun Body.inContactWith(other: Body): Boolean =
    position.distanceTo(other.position) <= (radius + other.radius)

fun centerOfMass(a: Body, b: Body): Position {
    val totalMass = a.mass + b.mass

    val x = ((a.position.x * a.mass.value) + (b.position.x * b.mass.value)) / (totalMass.value)
    val y = ((a.position.y * a.mass.value) + (b.position.y * b.mass.value)) / (totalMass.value)

    return Position(x, y)
}
