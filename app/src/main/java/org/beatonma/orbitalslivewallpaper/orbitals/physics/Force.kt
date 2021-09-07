package org.beatonma.orbitalslivewallpaper.orbitals.physics

val Float.newtons: Force get() = Force(this)
val Number.newtons: Force get() = this.toFloat().newtons

@JvmInline
value class Force(
    /** kg·m/s2 */
    val newtons: Float,
) {
    operator fun times(factor: Int) = (factor * newtons).newtons
    operator fun times(factor: Float) = (factor * newtons).newtons
    operator fun div(mass: Mass) = Acceleration(newtons / mass.kg)

    override fun toString(): String = "${newtons}N"
}
