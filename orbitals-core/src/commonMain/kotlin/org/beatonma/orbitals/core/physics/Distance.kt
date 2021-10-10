package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

val Float.km: Distance get() = Distance(this * 1000f)
val Number.km: Distance get() = this.toFloat().km

val Float.metres: Distance get() = Distance(this)
val Number.metres: Distance get() = this.toFloat().metres

/**
 * Metres
 */
@JvmInline
value class Distance internal constructor(
    override val value: Float,
) : Scalar {
    operator fun times(multiplier: Int) = Distance(value * multiplier)
    operator fun times(multiplier: Float) = Distance(value * multiplier)
    operator fun times(other: Distance) = Area(value * other.value)

    operator fun plus(other: Distance) = Distance(value + other.value)
    operator fun minus(other: Distance) = Distance(value - other.value)

    operator fun div(other: Distance): Float = value / other.value
    operator fun div(factor: Float): Distance = (value / factor).metres

    @OptIn(ExperimentalTime::class)
    operator fun div(duration: Duration): Speed =
        Speed(this.value / duration.toDouble(DurationUnit.SECONDS))

    operator fun compareTo(other: Distance): Int = this.value.compareTo(other.value)
    operator fun compareTo(other: Float): Int = this.value.compareTo(other)

    operator fun unaryMinus(): Distance = Distance(-value)

    override fun toString(): String = "${value}m"
}

val Distance.squared: Float get() = squareOf(this.value)
fun Distance.coerceAtLeast(min: Distance): Distance {
    return this.value.coerceAtLeast(min.value).metres
}

operator fun Float.times(distance: Distance) = distance * this
