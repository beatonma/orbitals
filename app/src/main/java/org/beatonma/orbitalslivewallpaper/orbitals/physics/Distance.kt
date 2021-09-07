package org.beatonma.orbitalslivewallpaper.orbitals.physics

import org.beatonma.orbitalslivewallpaper.debug
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

val Float.km: Distance get() = Distance(this * 1000f)
val Number.km: Distance get() = this.toFloat().km

val Float.metres: Distance get() = Distance(this)
val Number.metres: Distance get() = this.toFloat().metres

@JvmInline
value class Distance(val metres: Float) {
    operator fun times(multiplier: Int) = Distance(metres * multiplier)
    operator fun times(multiplier: Float) = Distance(metres * multiplier)

    operator fun plus(other: Distance) = Distance(metres + other.metres)
    operator fun minus(other: Distance) = Distance(metres - other.metres)

    operator fun div(other: Distance): Float = metres / other.metres
    operator fun div(factor: Float): Distance = (metres / factor).metres

    /**
     * Speed = distance / time
     */
    @OptIn(ExperimentalTime::class)
    operator fun div(duration: Duration): Speed = Speed(this.metres / duration.toDouble(DurationUnit.SECONDS))

    operator fun compareTo(other: Distance): Int = this.metres.compareTo(other.metres)
    operator fun compareTo(other: Float): Int = this.metres.compareTo(other)

    operator fun unaryMinus(): Distance = Distance(-metres)

    override fun toString(): String = "${metres}m"
}

val Distance.squared: Float get() = squareOf(this.metres)
fun Distance.coerceAtLeast(min: Distance): Distance {
    return this.metres.coerceAtLeast(min.metres).metres
}
