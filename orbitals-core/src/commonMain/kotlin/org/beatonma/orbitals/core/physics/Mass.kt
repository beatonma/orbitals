package org.beatonma.orbitals.core.physics

import Volume
import org.beatonma.orbitals.core.format
import org.beatonma.orbitals.core.util.warn
import kotlin.jvm.JvmInline

val Float.kg: Mass get() = Mass(this)
val Number.kg: Mass get() = this.toFloat().kg

/**
 * Kilograms
 */
@JvmInline
value class Mass internal constructor(override val value: Float) : Scalar {
    init {
        check(value >= 0f) { "Mass initiated with invalid value: $value" }
    }

    override fun toString() = "${value.format()}kg"

    operator fun plus(other: Mass) = Mass(value + other.value)
    operator fun minus(other: Mass): Mass {
        val result = value - other.value
        return if (result < 0f) {
            if (result < -.05f) {
                // Ignore if very close to zero
                warn("Negative mass! ($this - $other = $result)")
            }
            ZeroMass
        } else Mass(result)
    }

    operator fun times(multiplier: Float): Mass = (value * multiplier).kg
    operator fun times(other: Mass): Float = value * other.value
    operator fun times(velocity: Velocity): Momentum = velocity * this

    operator fun div(distance: Distance): Float = value / distance.value
    operator fun div(factor: Float): Mass = (value / factor).kg
    operator fun div(other: Mass): Float = value / other.value
    operator fun div(density: Density): Volume = Volume(value / density.value)
}

operator fun Float.times(mass: Mass) = mass * this
