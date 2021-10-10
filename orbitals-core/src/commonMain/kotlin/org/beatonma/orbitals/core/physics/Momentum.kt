package org.beatonma.orbitals.core.physics

data class MomentumScalar(override val value: Float) : Scalar {
    constructor(value: Number): this(value.toFloat())

    operator fun plus(other: MomentumScalar) = MomentumScalar(value + other.value)
    operator fun times(factor: Float) = MomentumScalar(value * factor)
    operator fun div(mass: Mass): Speed = Speed(value / mass.value)
    operator fun div(speed: Speed): Mass = Mass(value / speed.value)
}

data class Momentum internal constructor(
    override val x: MomentumScalar,
    override val y: MomentumScalar,
) : Vector2D<MomentumScalar> {
    constructor(x: Number, y: Number) : this(MomentumScalar(x), MomentumScalar(y))

    override val magnitude: MomentumScalar
        get() = MomentumScalar(magnitude(x, y))

    operator fun plus(other: Momentum): Momentum = Momentum(x + other.x, y + other.y)
    operator fun times(factor: Float): Momentum = Momentum(x * factor, y * factor)
    operator fun div(mass: Mass): Velocity {
        check(mass.value > 0f) { "Trying to divide Momentum by invalid mass: $mass" }
        return Velocity(x / mass, y / mass)
    }
    operator fun div(velocity: Velocity): Mass {
        val velMagnitude = velocity.magnitude.value
        check(velMagnitude > 0f) { "Trying to divide Momentum by invalid velocity $velocity" }
        return Mass(magnitude.value / velMagnitude)
    }

    override fun toString(): String {
        return "Momentum(x=${x.value}, y=${y.value})"
    }
}

operator fun Float.times(momentumScalar: MomentumScalar) = momentumScalar * this
operator fun Float.times(momentum: Momentum) = momentum * this
