package org.beatonma.orbitalslivewallpaper.orbitals.physics


val Speed.perSecond: Acceleration get() = Acceleration(magnitude)

@JvmInline
value class Acceleration(
    /** ms^-2 */
    val value: Float
)

data class AccelerationDelta(
    val value: Float,
    val x: Float,
    val y: Float
)
