package org.beatonma.orbitals.core.physics

import kotlin.math.atan2


interface Vector2D<T : Scalar> : Comparable<Vector2D<T>> {
    val x: T
    val y: T
    val magnitude: T

    val angle: Angle
        get() = atan2(y.value, x.value).radians

    val direction: Direction get() = angle.toDirection()

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

    fun toGeneric() = GenericVector2D(x, y)
}

/**
 * Return the dot product of vectors [a] and [b].
 */
fun <T : Scalar> dot(a: Vector2D<T>, b: Vector2D<T>): Float =
    (a.x.value * b.x.value) + (a.y.value * b.y.value)


/**
 * A generic Vector2D implementation, occasionally useful as a 'stepping stone' for parts of equations.
 * Must only be used within calculations, never as an input or output!
 */
data class GenericVector2D internal constructor(
    override val x: GenericScalar,
    override val y: GenericScalar,
) : Vector2D<GenericScalar> {
    constructor(x: Number, y: Number) : this(GenericScalar(x), GenericScalar(y))
    constructor(x: Scalar, y: Scalar) : this(GenericScalar(x), GenericScalar(y))

    override val magnitude: GenericScalar
        get() = GenericScalar(magnitude(x, y))

    operator fun <X : Scalar> plus(other: Vector2D<X>) = GenericVector2D(x + other.x, y + other.y)
    operator fun <X : Scalar> minus(other: Vector2D<X>) = GenericVector2D(x - other.x, y - other.y)
    operator fun <X : Scalar> times(multiplier: X) = GenericVector2D(x * multiplier, y * multiplier)
    operator fun times(multiplier: Float) = GenericVector2D(x * multiplier, y * multiplier)

    operator fun <X : Scalar> div(other: X) = GenericVector2D(x / other, y / other)
    operator fun div(divisor: Float) = GenericVector2D(x / divisor, y / divisor)
}

operator fun Float.times(vector2D: GenericVector2D) = vector2D * this
