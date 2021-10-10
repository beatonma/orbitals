package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline

val Float.newtons: Force get() = Force(this)

/**
 * Newtons
 */
@JvmInline
value class Force internal constructor(
    /** kg·m/s2 */
    override val value: Float,
): Scalar {
    operator fun times(factor: Float) = (factor * value).newtons
    operator fun div(mass: Mass) = AccelerationScalar(value / mass.value)

    override fun toString(): String = "${value}N"
}

operator fun Float.times(force: Force) = force * this
