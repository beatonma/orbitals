package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.nextFloat
import kotlin.random.Random


/**
 * Return the position by travelling [distance] away from [parent] at [angle].
 */
fun getRadialPosition(
    parent: Position,
    distance: Distance,
    angle: Angle = Random.nextFloat(0f, 359f).degrees,
): Position =
    parent + Position(
        cos(angle) * distance,
        sin(angle) * distance,
    )

/**
 * Run the block using points from around the origin.
 * All points fall on the circle with the given radius.
 */
fun doAround(
    radius: Distance,
    start: Angle = 0f.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360f.rawDegrees,
    block: (degrees: Angle, x: Distance, y: Distance) -> Unit,
) {
    val stepSize = rotation / steps
    doAround(
        radius, start, center, stepSize,
        rotation = rotation - stepSize,
        block = block
    )
}

/**
 * Run the block using points from around the origin.
 * All points fall on the circle with the given radius.
 */
private fun doAround(
    radius: Distance,
    start: Angle,
    center: Position,
    stepSize: Angle,
    rotation: Angle,
    block: (degrees: Angle, x: Distance, y: Distance) -> Unit
) {
    for (deg in start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt) {
        val degrees = deg.toFloat().degrees
        val (x, y) = getRadialPosition(center, radius, degrees)
        block(degrees, x, y)
    }
}

/**
 * Returns the result of applying [transform] to [steps] equally-spaced points at distance [radius]
 * from the [center].
 */
fun <T> mapAround(
    radius: Distance,
    start: Angle = 0f.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360f.rawDegrees,
    transform: (degrees: Angle, x: Distance, y: Distance) -> T
): List<T> {
    val stepSize = rotation / steps
    return mapAround(
        radius, start, center, stepSize,
        rotation = rotation - stepSize,
        transform = transform
    )
}

/**
 * Returns the result of applying [transform] to the points at distance [radius]
 * from the [center] at intervals of angle [stepSize].
 */
private fun <T> mapAround(
    radius: Distance,
    start: Angle = 0f.degrees,
    center: Position = ZeroPosition,
    stepSize: Angle = 45f.degrees,
    rotation: Angle = 315f.degrees,
    transform: (degrees: Angle, x: Distance, y: Distance) -> T
): List<T> =
    (start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt).map { deg ->
        val degrees = deg.toFloat().degrees
        val (x, y) = getRadialPosition(center, radius, degrees)
        transform(degrees, x, y)
    }

private val Angle.asDegreesInt: Int get() = asDegrees.toInt()
