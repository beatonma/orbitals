package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/** ms^-2 */
@JvmInline
value class AccelerationScalar internal constructor(override val value: Float) : Scalar {
    operator fun plus(other: AccelerationScalar): AccelerationScalar =
        AccelerationScalar(value + other.value)

    operator fun times(multiplier: Float): AccelerationScalar =
        AccelerationScalar(value * multiplier)

    @OptIn(ExperimentalTime::class)
    operator fun times(duration: Duration): Speed {
        return Speed(value * duration.toDouble(DurationUnit.SECONDS).toFloat())
    }

    override fun toString(): String {
        return "$value"
    }
}

internal fun Acceleration(acceleration: AccelerationScalar, theta: Angle) = Acceleration(
    x = acceleration * cos(theta),
    y = acceleration * sin(theta),
)

data class Acceleration internal constructor(
    override val x: AccelerationScalar,
    override val y: AccelerationScalar,
) : Vector2D<AccelerationScalar> {
    @OptIn(ExperimentalTime::class)
    operator fun times(duration: Duration): Velocity =
        Velocity(
            x = x * duration,
            y = y * duration,
        )

    operator fun plus(other: Acceleration) = Acceleration(x + other.x, y + other.y)
    override val magnitude: AccelerationScalar = AccelerationScalar(magnitude(x, y))
}

operator fun Float.times(acceleration: AccelerationScalar) = acceleration * this

@OptIn(ExperimentalTime::class)
operator fun Duration.times(acceleration: Acceleration) = acceleration * this

@OptIn(ExperimentalTime::class)
operator fun Duration.times(acceleration: AccelerationScalar) = acceleration * this
