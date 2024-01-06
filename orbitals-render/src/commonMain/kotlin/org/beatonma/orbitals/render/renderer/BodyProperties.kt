package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.color.Color

typealias BodyPropertyMap = Map<UniqueID, BodyProperties>
class BodyProperties (
    val color: Color
)
