package org.beatonma.orbitals.physics

val Float.kg: Mass get() = Mass(this)
val Number.kg: Mass get() = this.toFloat().kg

@JvmInline
value class Mass(val kg: Float) {
    operator fun plus(other: Mass) = Mass(kg + other.kg)

    operator fun times(other: Mass): Float = kg * other.kg
    operator fun times(factor: Float): Mass = (kg * factor).kg

    operator fun div(distance: Distance): Float = kg / distance.metres
    operator fun div(factor: Float): Mass = (kg / factor).kg
}
