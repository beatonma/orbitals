package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.options.ColorOptions
import kotlin.random.Random

typealias BodyPropertyMap = Map<UniqueID, BodyProperties>

class BodyProperties(
    val color: Color,
    val colorAlt: Color,
    val seed: Float = Random.nextFloat(),
)

fun BodyProperties(body: Body, options: ColorOptions) = BodyProperties(
    color = options.colorFor(body),
    colorAlt = options.colorFor(body),
)
