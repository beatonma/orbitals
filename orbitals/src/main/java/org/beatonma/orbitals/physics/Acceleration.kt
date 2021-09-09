package org.beatonma.orbitals.physics

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime


val Speed.perSecond: AccelerationScalar get() = AccelerationScalar(magnitude)

@JvmInline
value class AccelerationScalar(
    /** ms^-2 */
    val value: Float
) {
    operator fun times(multiplier: Float) = AccelerationScalar(value * multiplier)

    @OptIn(ExperimentalTime::class)
    operator fun times(duration: Duration) = Speed(value * duration.toDouble(DurationUnit.SECONDS))
}

fun Acceleration(acceleration: AccelerationScalar, theta: Angle) = Acceleration(
    acceleration,
    x = acceleration * cos(theta),
    y = acceleration * sin(theta),
)

data class Acceleration(
    val value: AccelerationScalar,
    val x: AccelerationScalar,
    val y: AccelerationScalar,
) {
    @OptIn(ExperimentalTime::class)
    operator fun times(duration: Duration): Velocity =
        Velocity(
            x * duration,
            y * duration,
        )
}
