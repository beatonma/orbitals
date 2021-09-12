package org.beatonma.orbitals.physics

import kotlin.math.PI

@JvmInline
value class Angle internal constructor(override val value: Float): Scalar {
    val asRadians: Float get() = value
    val asDegrees: Float get() = value * (180f / PI).toFloat()

    operator fun plus(other: Angle) = Angle(this.value + other.value)
    operator fun minus(other: Angle) = Angle(this.value - other.value)

    operator fun times(factor: Float) = Angle(this.value * factor)
    operator fun times(factor: Int) = Angle(this.value * factor)

    operator fun div(divisor: Int) = Angle(this.value / divisor)
    operator fun div(divisor: Float) = Angle(this.value / divisor)

    override fun toString(): String {
        return "$asDegrees°"
    }
}


val Float.radians: Angle get() = Angle(this)
val Float.degrees: Angle get() = Angle((this * PI / 180.0).toFloat())

val Int.degrees: Angle get() = toFloat().degrees

fun sin(angle: Angle) = kotlin.math.sin(angle.value)
fun cos(angle: Angle) = kotlin.math.cos(angle.value)
