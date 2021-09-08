package org.beatonma.orbitals.physics

import org.beatonma.orbitals.Space
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun Position(x: Number, y: Number) = Position(x.metres, y.metres)

data class Position(
    var x: Distance,
    var y: Distance,
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
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
    atan2((other.y - this.y).metres, (other.x - this.x).metres).radians

fun Position.distanceTo(other: Position): Distance = Distance(
    sqrt(
        squareOf(other.x - this.x)
                + squareOf(other.y - this.y)
    ).absoluteValue
)

fun Space.contains(position: Position): Boolean =
    contains(position.x.metres.roundToInt(), position.y.metres.roundToInt())
