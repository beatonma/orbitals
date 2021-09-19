package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.options.PhysicsOptions


data class Options(
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
    val frameRate: Int = 60,
)
