package org.beatonma.orbitals.physics

import kotlin.random.Random


/**
 * Return the position by travelling [distance] away from [parent] at [angle].
 */
fun getRadialPosition(
    parent: Position,
    distance: Distance,
    angle: Angle = Random.nextInt(0, 359).degrees,
): Position =
    parent + Position(
    cos(angle) * distance.value,
    sin(angle) * distance.value,
)

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
    block: (degrees: Int, x: Distance, y: Distance) -> Unit
) {
    for (deg in start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt) {
        val (x, y) = getRadialPosition(center, radius, deg.degrees)
        block(deg, x, y)
    }
}

fun doAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360.rawDegrees,
    block: (degrees: Int, x: Distance, y: Distance) -> Unit,
) {
    val stepSize = rotation / steps
    doAround(
        radius, start, center, stepSize,
        rotation = rotation - stepSize,
        block = block
    )
}

/**
 * Returns the result of applying [transform] to the points at distance [radius]
 * from the [center] at intervals of angle [stepSize].
 */
private fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    stepSize: Angle = 45.degrees,
    rotation: Angle = 315.degrees,
    transform: (degrees: Int, x: Distance, y: Distance) -> T
): List<T> =
    (start.asDegreesInt..rotation.asDegreesInt step stepSize.asDegreesInt).map { deg ->
        val (x, y) = getRadialPosition(center, radius, deg.degrees)
        transform(deg, x, y)
    }

/**
 * Returns the result of applying [transform] to [steps] equally-spaced points at distance [radius]
 * from the [center].
 */
fun <T> mapAround(
    radius: Distance,
    start: Angle = 0.degrees,
    center: Position = ZeroPosition,
    steps: Int = 8,
    rotation: Angle = 360.rawDegrees,
    transform: (degrees: Int, x: Distance, y: Distance) -> T
): List<T> {
    val stepSize = rotation / steps
    return mapAround(
        radius, start, center, stepSize,
        rotation = rotation - stepSize,
        transform = transform
    )
}

private val Angle.asDegreesInt: Int get() = asDegrees.toInt()
