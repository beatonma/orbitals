package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.format
import org.beatonma.orbitals.core.squareOf
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.DurationUnit

val Float.metres: Distance get() = Distance(this)

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
    operator fun div(factor: Float): Distance = Distance(value / factor)

    operator fun div(duration: Duration): Speed =
        Speed(value / duration.toDouble(DurationUnit.SECONDS).toFloat())

    operator fun compareTo(other: Distance): Int = value.compareTo(other.value)
    operator fun compareTo(other: Float): Int = value.compareTo(other)

    operator fun unaryMinus(): Distance = Distance(-value)

    override fun toString(): String = "${value.format()}m"
}

val Distance.squared: Area get() = Area(squareOf(value))
fun Distance.coerceAtLeast(min: Distance): Distance = Distance(value.coerceAtLeast(min.value))

operator fun Float.times(distance: Distance) = distance * this
