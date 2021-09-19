package org.beatonma.orbitals.core.physics

interface Vector2D<T: Scalar>: Comparable<Vector2D<T>> {
    val x: T
    val y: T
    val magnitude: T

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
