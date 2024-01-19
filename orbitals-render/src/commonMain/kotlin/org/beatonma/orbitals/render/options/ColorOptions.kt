package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.color.MaterialBlue
import org.beatonma.orbitals.render.color.MaterialColors
import org.beatonma.orbitals.render.color.MaterialGreen
import org.beatonma.orbitals.render.color.MaterialGrey
import org.beatonma.orbitals.render.color.MaterialOrange
import org.beatonma.orbitals.render.color.MaterialPink
import org.beatonma.orbitals.render.color.MaterialPurple
import org.beatonma.orbitals.render.color.MaterialRed
import org.beatonma.orbitals.render.color.MaterialYellow

data class ColorOptions(
    val background: Color = Color.Black,
    val bodies: Set<ObjectColors> = setOf(
        ObjectColors.Blue,
        ObjectColors.Greyscale,
        ObjectColors.Yellow,
    ),
    val foregroundAlpha: Float = 1f,
) {
    fun colorFor(body: Body): Color = Color(bodies.random().colors.random())
}

enum class ObjectColors(val colors: Array<ULong>) {
    Greyscale(MaterialGrey),
    Red(MaterialRed),
    Orange(MaterialOrange),
    Yellow(MaterialYellow),
    Green(MaterialGreen),
    Blue(MaterialBlue),
    Purple(MaterialPurple),
    Pink(MaterialPink),
    Any(MaterialColors),
    ;
}
