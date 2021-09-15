package org.beatonma.orbitalslivewallpaper.orbitals.options

import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.options.VisualOptions


data class Options(
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
    val frameRate: Int = 60,
)
