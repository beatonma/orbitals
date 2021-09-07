package org.beatonma.orbitalslivewallpaper.orbitals.physics

import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val Distance.perSecond: Speed
    get() = this / Duration.seconds(1)

data class Velocity(
    var x: Speed = Speed(0.0),
    var y: Speed = Speed(0.0),
) {
    constructor(x: Number, y: Number) : this(Speed(x), Speed(y))

    operator fun plus(other: Velocity): Velocity = Velocity(this.x + other.x, this.y + other.y)

    val vector: Speed get() = velocityVector(x, y)
    val angle: Angle get() = atan2(y.magnitude, x.magnitude).radians

    override fun toString(): String =
        "$vector @ ${angle.asDegrees.roundToInt()}°"
}


data class Speed(private var metresPersSecond: Float) {
    /**
     * Metres per second.
     */
    val magnitude get() = metresPersSecond

    constructor(magnitude: Number) : this(magnitude.toFloat())

    /**
     * Distance = speed * time
     */
    @OptIn(ExperimentalTime::class)
    operator fun times(time: Duration): Distance = (this.magnitude * time.toDouble(DurationUnit.SECONDS)).metres
    operator fun times(multiplier: Float): Speed = Speed(magnitude * multiplier)

    operator fun plus(other: Speed) = Speed(magnitude + other.magnitude)

    operator fun plusAssign(other: Float) {
        metresPersSecond += other
    }

    operator fun minusAssign(other: Float) {
        metresPersSecond -= other
    }

    operator fun unaryMinus(): Speed = Speed(-magnitude)

    override fun toString(): String = "${magnitude}m/s"
}
