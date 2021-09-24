package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.engine.Space
import kotlin.math.atan2
import kotlin.math.roundToInt

fun Position(x: Number, y: Number) = Position(x.metres, y.metres)

data class Position(
    override var x: Distance,
    override var y: Distance,
) : Vector2D<Distance> {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    override val magnitude: Distance get() = Distance(magnitude(x, y))
}


fun Position.gradientTo(other: Position, max: Float = 1.0e5f): Float {
    val raw = (other.y - this.y) / (other.x - this.x)

    return when {
        raw.isNaN() -> 0.0f
        raw == Float.POSITIVE_INFINITY -> max
        raw == Float.NEGATIVE_INFINITY -> -max
        else -> raw.coerceIn(-max, max)
    }
}

fun Position.angleTo(other: Position): Angle =
    atan2((other.y - this.y).value, (other.x - this.x).value).radians

fun Position.distanceTo(other: Position): Distance =
    sqrt(
        squareOf(other.x - this.x)
                + squareOf(other.y - this.y)
    )

fun centerOf(a: Position, b: Position) = Position((b.x + a.x) / 2f, (b.y + a.y) / 2f)

fun Space.contains(position: Position): Boolean =
    contains(position.x.value.roundToInt(), position.y.value.roundToInt())

private fun squareOf(distance: Distance): Area = distance * distance
