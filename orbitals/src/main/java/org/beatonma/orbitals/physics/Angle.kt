package org.beatonma.orbitals.physics

import kotlin.math.PI

@JvmInline
value class Angle internal constructor(val asRadians: Float) {
    val asDegrees: Float get() = asRadians * (180f / PI).toFloat()

    operator fun plus(other: Angle) = Angle(this.asRadians + other.asRadians)
    operator fun minus(other: Angle) = Angle(this.asRadians - other.asRadians)

    operator fun times(factor: Float) = Angle(this.asRadians * factor)
    operator fun times(factor: Int) = Angle(this.asRadians * factor)

    operator fun div(divisor: Int) = Angle(this.asRadians / divisor)
    operator fun div(divisor: Float) = Angle(this.asRadians / divisor)
}


val Float.radians: Angle get() = Angle(this)
val Float.degrees: Angle get() = Angle((this * PI / 180.0).toFloat())

val Number.radians: Angle get() = this.toFloat().radians
val Number.degrees: Angle get() = this.toFloat().degrees

val Angle.asDegreesInt: Int get() = asDegrees.toInt()
fun sin(angle: Angle) = kotlin.math.sin(angle.asRadians)
fun cos(angle: Angle) = kotlin.math.cos(angle.asRadians)
