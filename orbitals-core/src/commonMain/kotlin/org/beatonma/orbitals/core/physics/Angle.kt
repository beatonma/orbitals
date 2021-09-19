package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val PI_FLOAT = PI.toFloat()
private const val CIRCLE_RADIANS = (2.0 * PI).toFloat()

@JvmInline
value class Angle internal constructor(override val value: Float): Scalar {
    val asRadians: Float get() = value
    val asDegrees: Float get() = value * (180f / PI_FLOAT)

    operator fun plus(other: Angle) = Angle(this.value + other.value)
    operator fun minus(other: Angle) = Angle(this.value - other.value)

    operator fun times(factor: Float) = Angle(this.value * factor)
    operator fun times(factor: Int) = Angle(this.value * factor)

    operator fun div(divisor: Int) = Angle(this.value / divisor)
    operator fun div(divisor: Float) = Angle(this.value / divisor)

    override fun toString(): String = "$asDegrees°"
}

private val Float.positiveRadians: Angle get() =
    when {
        this < 0f -> Angle(CIRCLE_RADIANS - (-this % CIRCLE_RADIANS))
        else -> Angle(this % CIRCLE_RADIANS)
    }

val Float.radians: Angle get() = positiveRadians
val Float.degrees: Angle get() = (this * PI_FLOAT / 180f).positiveRadians
val Number.degrees: Angle get() = toFloat().degrees
val Number.rawDegrees: Angle get() = Angle(this.toFloat() * PI_FLOAT / 180f)

fun sin(angle: Angle) = sin(angle.value)
fun cos(angle: Angle) = cos(angle.value)
