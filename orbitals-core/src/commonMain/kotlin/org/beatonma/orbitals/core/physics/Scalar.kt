package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.math.sqrt

interface Scalar : Comparable<Scalar> {
    val value: Float

    override fun compareTo(other: Scalar): Int {
        if (this::class == other::class) {
            return value.compareTo(other.value)
        }
        throw IllegalArgumentException(
            "Cannot compare different Scalar implementations: ${this::class} vs ${other::class}"
        )
    }

    fun isZero(): Boolean = value == 0f
}

private fun sqrt(scalar: Scalar): Float = sqrt(scalar.value)
private fun squareOf(scalar: Scalar): Float = scalar.value * scalar.value
internal fun <T : Scalar> magnitude(x: T, y: T): Float = sqrt(squareOf(x) + squareOf(y))

operator fun <T : Scalar> T.div(other: T): Float = this.value / other.value

@JvmInline
value class GenericScalar(override val value: Float) : Scalar {
    operator fun times(multiplier: Float) = GenericScalar(value * multiplier)
    operator fun div(divisor: Float) = GenericScalar(value / divisor)

    operator fun <T : Scalar> plus(other: T) = GenericScalar(value + other.value)
    operator fun <T : Scalar> minus(other: T) = GenericScalar(value - other.value)
    operator fun <T : Scalar> times(other: T) = GenericScalar(value * other.value)
    operator fun <T : Scalar> div(divisor: T) = GenericScalar(value / divisor.value)
}

operator fun Float.times(scalar: GenericScalar) = scalar * this
