package org.beatonma.orbitals.core.physics

import kotlin.math.atan2
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

private const val PI = 3.141592653589793

@OptIn(ExperimentalTime::class)
val Distance.perSecond: Speed
    get() = this / Duration.seconds(1)

data class Velocity internal constructor(
    override var x: Speed = Speed(0.0),
    override var y: Speed = Speed(0.0),
) : Vector2D<Speed> {
    constructor(x: Number, y: Number) : this(Speed(x), Speed(y))

    operator fun plus(other: Velocity): Velocity =
        Velocity(
            x = this.x + other.x,
            y = this.y + other.y
        )

    operator fun plusAssign(other: Velocity) {
        this.x += other.x
        this.y += other.y
    }

    override val magnitude: Speed get() = Speed(magnitude(x, y))
    val angle: Angle
        get() {
            val a = atan2(y.value, x.value)
            return if (a < 0f) ((PI * 2f).toFloat() + a).radians
            else a.radians
        }

    override fun toString(): String = "Velocity($x, $y | $angle)"
}


/**
 * Metres per second
 */
data class Speed internal constructor(override var value: Float) : Scalar {
    constructor(speed: Number) : this(speed.toFloat())

    @OptIn(ExperimentalTime::class)
    operator fun times(time: Duration): Distance =
        (this.value * time.toDouble(DurationUnit.SECONDS)).metres

    operator fun times(multiplier: Float): Speed = Speed(value * multiplier)

    operator fun plus(other: Speed) = Speed(value + other.value)

    operator fun plusAssign(other: Float) {
        value += other
    }

    operator fun minusAssign(other: Float) {
        value -= other
    }

    operator fun unaryMinus(): Speed = Speed(-value)

    override fun toString(): String = "${value}m/s"
}
