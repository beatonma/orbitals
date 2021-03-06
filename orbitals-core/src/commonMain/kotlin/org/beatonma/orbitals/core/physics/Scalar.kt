package org.beatonma.orbitals.core.physics

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

    fun toGeneric() = GenericScalar(this)
}

private fun sqrt(scalar: Scalar): Float = sqrt(scalar.value)
private fun squareOf(scalar: Scalar): Float = scalar.value * scalar.value
internal fun magnitude(x: Scalar, y: Scalar): Float = sqrt(squareOf(x) + squareOf(y))

data class GenericScalar(override val value: Float): Scalar {
    constructor(value: Number): this(value.toFloat())
    constructor(value: Scalar): this(value.value)

    operator fun times(multiplier: Float) = GenericScalar(value * multiplier)
    operator fun div(divisor: Float) = GenericScalar(value / divisor)

    operator fun <T: Scalar> plus(other: T) = GenericScalar(value + other.value)
    operator fun <T: Scalar> minus(other: T) = GenericScalar(value - other.value)
    operator fun <T: Scalar> times(other: T) = GenericScalar(value * other.value)
    operator fun <T: Scalar> div(divisor: T) = GenericScalar(value / divisor.value)
}

operator fun Float.times(scalar: GenericScalar) = scalar * this
