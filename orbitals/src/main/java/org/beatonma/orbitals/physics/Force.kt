package org.beatonma.orbitals.physics

val Float.newtons: Force get() = Force(this)

/**
 * Newtons
 */
@JvmInline
value class Force internal constructor(
    /** kg·m/s2 */
    override val value: Float,
): Scalar {
    operator fun times(factor: Int) = (factor * value).newtons
    operator fun times(factor: Float) = (factor * value).newtons
    operator fun div(mass: Mass) = AccelerationScalar(value / mass.value)

    override fun toString(): String = "${value}N"
}
