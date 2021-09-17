package org.beatonma.orbitals.physics

import kotlin.math.sqrt

interface Scalar: Comparable<Scalar> {
    val value: Float

    override fun compareTo(other: Scalar): Int {
        if (this::class == other::class) {
            return value.compareTo(other.value)
        }
        throw IllegalArgumentException(
            "Cannot compare different Scalar implementations: ${this::class} vs ${other::class}"
        )
    }
}

private fun sqrt(scalar: Scalar): Float = sqrt(scalar.value)
private fun squareOf(scalar: Scalar): Float = scalar.value * scalar.value
internal fun magnitude(x: Scalar, y: Scalar): Float = sqrt(squareOf(x) + squareOf(y))
