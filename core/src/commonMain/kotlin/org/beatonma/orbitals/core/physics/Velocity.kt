package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.format
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.DurationUnit

val Float.metresPerSecond: Speed get() = Speed(this)

data class Velocity internal constructor(
    override val x: Speed = Speed(0f),
    override val y: Speed = Speed(0f),
) : Vector2D<Speed> {
    override val magnitude: Speed get() = Speed(magnitude(x, y))

    operator fun plus(other: Velocity): Velocity =
        Velocity(
            x = this.x + other.x,
            y = this.y + other.y
        )

    operator fun minus(other: Velocity): Velocity =
        Velocity(
            x = this.x - other.x,
            y = this.y - other.y
        )

    operator fun times(multiplier: Float): Velocity = Velocity(x * multiplier, y * multiplier)
    operator fun times(mass: Mass) = Momentum(x * mass, y * mass)
    operator fun times(timeDelta: Duration) = Position(x * timeDelta, y * timeDelta)
    operator fun div(factor: Float): Velocity = Velocity(x / factor, y / factor)

    override fun toString(): String = "Velocity($x, $y | $angle)"
}


/**
 * Metres per second
 */
@JvmInline
value class Speed internal constructor(override val value: Float) : Scalar {
    operator fun times(time: Duration): Distance =
        (this.value * time.toDouble(DurationUnit.SECONDS).toFloat()).metres

    operator fun times(multiplier: Float): Speed = Speed(value * multiplier)
    operator fun times(mass: Mass): MomentumScalar = MomentumScalar(value * mass.value)

    operator fun div(factor: Float): Speed = Speed(value / factor)

    operator fun plus(other: Speed) = Speed(value + other.value)
    operator fun minus(other: Speed) = Speed(value - other.value)

    operator fun unaryMinus(): Speed = Speed(-value)

    override fun toString(): String = "${value.format()}m/s"
}

operator fun Float.times(speed: Speed) = speed * this
operator fun Float.times(velocity: Velocity) = velocity * this

fun Velocity(magnitude: Speed, theta: Angle) = Velocity(
    x = magnitude * cos(theta),
    y = magnitude * sin(theta),
)

fun Velocity.rotateBy(angle: Angle): Velocity = Velocity(
    (cos(angle) * x) - (sin(angle) * y),
    (sin(angle) * x) + (cos(angle) * y)
)
