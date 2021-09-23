package org.beatonma.orbitals.core.physics

import kotlin.math.atan2
private const val PI = 3.141592653589793

interface Vector2D<T: Scalar>: Comparable<Vector2D<T>> {
    val x: T
    val y: T
    val magnitude: T

    val angle: Angle
        get() {
            val a = atan2(y.value, x.value)
            return if (a < 0f) ((PI * 2f).toFloat() + a).radians
            else a.radians
        }

    override fun compareTo(other: Vector2D<T>): Int {
        if (this::class == other::class) {

            return when (val xResult = x.compareTo(other.x)) {
                0 -> y.compareTo(other.y)
                else -> xResult
            }
        }

        throw IllegalArgumentException(
            "Cannot compare different Vector2D implementations: ${this::class} vs ${other::class}"
        )
    }
}
