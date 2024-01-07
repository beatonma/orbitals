package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.format
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


val Distance.perSecond: Speed
    get() = this / 1.seconds

data class Velocity internal constructor(
    override var x: Speed = Speed(0f),
    override var y: Speed = Speed(0f),
) : Vector2D<Speed> {
    constructor(x: Number, y: Number) : this(Speed(x), Speed(y))

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

    operator fun minusAssign(other: Velocity) {
        this.x -= other.x
        this.y -= other.y
    }

    operator fun times(multiplier: Float): Velocity = Velocity(x * multiplier, y * multiplier)
    operator fun times(mass: Mass) = Momentum(x * mass, y * mass)

    override fun toString(): String = "Velocity($x, $y | $angle)"

    fun isZero(): Boolean = x.value == 0f && y.value == 0f
}


/**
 * Metres per second
 */
data class Speed internal constructor(override val value: Float) : Scalar {
    constructor(speed: Number) : this(speed.toFloat())

    operator fun times(time: Duration): Distance =
        (this.value * time.toDouble(DurationUnit.SECONDS)).metres

    operator fun times(multiplier: Float): Speed = Speed(value * multiplier)
    operator fun times(mass: Mass): MomentumScalar = MomentumScalar(value * mass.value)

    operator fun plus(other: Speed) = Speed(value + other.value)
    operator fun minus(other: Speed) = Speed(value - other.value)

    operator fun unaryMinus(): Speed = Speed(-value)

    override fun toString(): String = "${value.format()}m/s"
}

operator fun Float.times(speed: Speed) = speed * this
operator fun Float.times(velocity: Velocity) = velocity * this
