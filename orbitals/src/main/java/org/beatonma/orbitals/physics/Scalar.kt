package org.beatonma.orbitals.physics

import kotlin.math.sqrt

interface Scalar {
    val value: Float
}

private fun sqrt(scalar: Scalar): Float = sqrt(scalar.value)
private fun squareOf(scalar: Scalar): Float = scalar.value * scalar.value
internal fun magnitude(x: Scalar, y: Scalar): Float = sqrt(squareOf(x) + squareOf(y))
