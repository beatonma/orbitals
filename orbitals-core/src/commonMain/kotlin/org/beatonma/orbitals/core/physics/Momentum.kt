package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.divideUnevenly
import org.beatonma.orbitals.core.format
import kotlin.jvm.JvmInline

@JvmInline
value class MomentumScalar(override val value: Float) : Scalar {
    constructor(value: Number) : this(value.toFloat())

    operator fun plus(other: MomentumScalar) = MomentumScalar(value + other.value)
    operator fun minus(other: MomentumScalar) = MomentumScalar(value - other.value)
    operator fun times(factor: Float) = MomentumScalar(value * factor)
    operator fun div(mass: Mass): Speed = Speed(value / mass.value)
    operator fun div(speed: Speed): Mass = Mass(value / speed.value)

    override fun toString(): String = "${value.format()}kgm/s"
}

data class Momentum internal constructor(
    override val x: MomentumScalar,
    override val y: MomentumScalar,
) : Vector2D<MomentumScalar> {
    constructor(x: Number, y: Number) : this(MomentumScalar(x), MomentumScalar(y))

    override val magnitude: MomentumScalar
        get() = MomentumScalar(magnitude(x, y))

    operator fun plus(other: Momentum): Momentum = Momentum(x + other.x, y + other.y)
    operator fun minus(other: Momentum): Momentum = Momentum(x - other.x, y - other.y)
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

    override fun toString(): String = "Momentum(x=${x}, y=${y})"
}

operator fun Float.times(momentumScalar: MomentumScalar) = momentumScalar * this
operator fun Float.times(momentum: Momentum) = momentum * this

fun Momentum.divideUnevenly(divisor: Int): List<Momentum> {
    val xValue = x.value
    val yValue = y.value

    return xValue.divideUnevenly(divisor)
        .zip(yValue.divideUnevenly(divisor))
        .map { (a, b) ->
            Momentum(MomentumScalar(a), MomentumScalar(b))
        }
}
