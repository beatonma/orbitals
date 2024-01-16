package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private const val PI_FLOAT = PI.toFloat()
private const val CIRCLE_RADIANS = (2.0 * PI).toFloat()

@JvmInline
value class Angle internal constructor(override val value: Float) : Scalar {
    val asRadians: Float get() = value
    val asDegrees: Float get() = value * (180f / PI_FLOAT)

    operator fun plus(other: Angle) = Angle(this.value + other.value)
    operator fun minus(other: Angle) = Angle(this.value - other.value)

    operator fun times(factor: Float) = Angle(this.value * factor)
    operator fun times(distance: Distance) =
        Position(cos(this) * distance.value, sin(this) * distance.value)

    operator fun div(divisor: Int) = Angle(this.value / divisor)
    operator fun div(divisor: Float) = Angle(this.value / divisor)

    internal fun toDirection() = Direction(this)
    override fun toString(): String = "$asDegrees°"
    operator fun unaryMinus(): Angle = Angle(-value)
}

private class ClosedAngleRange(
    override val start: Angle,
    override val endInclusive: Angle
) : ClosedFloatingPointRange<Angle> {
    override fun lessThanOrEquals(a: Angle, b: Angle): Boolean = a.value <= b.value
}

operator fun Angle.rangeTo(that: Angle): ClosedFloatingPointRange<Angle> =
    ClosedAngleRange(this, that)

class Direction private constructor(
    override val x: Distance,
    override val y: Distance,
) : Vector2D<Distance> {

    internal constructor(angle: Angle) : this(cos(angle).metres, sin(angle).metres)
    internal constructor(x: Number, y: Number) : this(x.metres, y.metres)

    override val magnitude: Distance
        get() = Distance(magnitude(x, y))

    operator fun times(distance: Distance) =
        Position(x.value * distance.value, y.value * distance.value)

    fun toAngle() = atan2(y.value, x.value).radians

    override fun toString(): String = "Direction(${x.value}, ${y.value}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Direction

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}


val Float.radians: Angle get() = positiveRadians
val Float.degrees: Angle get() = (this * PI_FLOAT / 180f).positiveRadians
val Number.degrees: Angle get() = toFloat().degrees
val Number.rawDegrees: Angle get() = Angle(this.toFloat() * PI_FLOAT / 180f)

fun sin(angle: Angle) = sin(angle.value)
fun cos(angle: Angle) = cos(angle.value)

operator fun Float.times(angle: Angle) = angle * this

private val Float.positiveRadians: Angle
    get() =
        when {
            this < 0f -> Angle(CIRCLE_RADIANS - (-this % CIRCLE_RADIANS))
            else -> Angle(this % CIRCLE_RADIANS)
        }
