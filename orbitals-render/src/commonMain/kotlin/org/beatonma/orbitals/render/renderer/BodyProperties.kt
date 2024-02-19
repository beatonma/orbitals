package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.color.Color
import kotlin.random.Random

typealias BodyPropertyMap = Map<UniqueID, BodyProperties>

class BodyProperties(
    val color: Color,
    val seed: Float = Random.nextFloat(),
)
