package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline

val Float.kg: Mass get() = Mass(this)
val Number.kg: Mass get() = this.toFloat().kg

/**
 * Kilograms
 */
@JvmInline
value class Mass internal constructor(override val value: Float): Scalar {
    operator fun plus(other: Mass) = Mass(value + other.value)

    operator fun times(other: Mass): Float = value * other.value
    operator fun times(factor: Float): Mass = (value * factor).kg

    operator fun div(distance: Distance): Float = value / distance.value
    operator fun div(factor: Float): Mass = (value / factor).kg
}
