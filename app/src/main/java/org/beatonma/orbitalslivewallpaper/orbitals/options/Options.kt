package org.beatonma.orbitalslivewallpaper.orbitals.options

import org.beatonma.orbitals.options.PhysicsOptions


data class Options(
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
    val frameRate: Int = 60,
)
