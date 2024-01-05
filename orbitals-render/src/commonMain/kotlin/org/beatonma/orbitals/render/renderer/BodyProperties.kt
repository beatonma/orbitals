package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.UniqueID

typealias BodyPropertyMap = Map<UniqueID, BodyProperties>
class BodyProperties (
    val color: Int
)
