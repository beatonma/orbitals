package org.beatonma.orbitals.options


data class Options(
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
    val frameRate: Int = 60,
)
