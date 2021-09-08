package org.beatonma.orbitals.physics

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


fun squareOf(value: Float): Float = value.pow(2.0F)
fun squareOf(distance: Distance): Float = distance.metres.pow(2)

fun hypotenuse(x: Float, y: Float): Float =
    sqrt(squareOf(x) + squareOf(y))

fun velocityVector(x: Speed, y: Speed): Speed =
    hypotenuse(x.magnitude, y.magnitude).metres.perSecond

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

/**
 * Returns the result of applying [transform] to the points at distance [radius]
 * from the [center] at intervals of angle [stepSize].
 */
inline fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    stepSize: Angle = 45.degrees,
    rotation: Angle = 315.degrees,
    transform: (degrees: Int, x: Float, y: Float) -> T
): List<T> {
    return (start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt).map { deg ->
        val theta = deg.degrees.asRadians

        val x = center.x.metres + (cos(theta) * radius.metres)
        val y = center.y.metres + (sin(theta) * radius.metres)

        transform(deg, x, y)
    }
}

/**
 * Returns the result of applying [transform] to [steps] equally-spaced points at distance [radius]
 * from the [center].
 */
inline fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360.degrees,
    transform: (degrees: Int, x: Float, y: Float) -> T
): List<T> {
    val stepSize = rotation / steps
    return mapAround(
        radius, start, center, stepSize,
        rotation = rotation - stepSize,
        transform = transform
    )
}
