package org.beatonma.orbitalslivewallpaper.orbitals.physics

import org.beatonma.orbitalslivewallpaper.debug
import kotlin.math.*


fun squareOf(value: Float): Float = value.pow(2.0F)
fun squareOf(distance: Distance): Float = distance.metres.pow(2)

fun hypotenuse(x: Float, y: Float): Float =
    sqrt(squareOf(x) + squareOf(y))

fun velocityVector(x: Speed, y: Speed): Speed =
    hypotenuse(x.magnitude, y.magnitude).metres.perSecond

@JvmInline
value class Angle(val asRadians: Float) {
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
fun sin(angle: Angle) = sin(angle.asRadians)
fun cos(angle: Angle) = cos(angle.asRadians)

/**
 * Run the block using points from around the origin.
 * All points fall on the circle with the given radius.
 */
inline fun doAround(
    radius: Float,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    stepSize: Angle = 45.degrees,
    rotation: Angle = 315.degrees,
    block: (degrees: Int, x: Float, y: Float) -> Unit
) {
    for (deg in start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt) {
        val theta = deg.degrees.asRadians

        val x = center.x.metres + (cos(theta) * radius)
        val y = center.y.metres + (sin(theta) * radius)

        block(deg, x, y)
    }
}

inline fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    stepSize: Angle = 45.degrees,
    rotation: Angle = 315.degrees,
    block: (degrees: Int, x: Float, y: Float) -> T
): List<T> {
    return (start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt).map { deg ->
        debug("deg: $deg (max $rotation)")
        val theta = deg.degrees.asRadians

        val x = center.x.metres + (cos(theta) * radius.metres)
        val y = center.y.metres + (sin(theta) * radius.metres)

        block(deg, x, y)
    }
}

inline fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360.degrees,
    block: (degrees: Int, x: Float, y: Float) -> T
): List<T> {
    val stepSize = rotation / steps
    return mapAround(
        radius, start, center, stepSize, rotation - stepSize, block
    )
}
