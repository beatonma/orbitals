package org.beatonma.orbitals.render.options

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
    val background: Int = 0x000000,
    val bodies: Set<ObjectColors> = setOf(
        ObjectColors.Greyscale,
    ),
    val foregroundAlpha: Float = 1f,
) {
    val colorForBody: Int get() = bodies.random().colors().random()
}

enum class ObjectColors {
    Greyscale,
    Red,
    Orange,
    Yellow,
    Green,
    Blue,
    Purple,
    Pink,
    Any,
    ;

    fun colors(): Array<Int> = when (this) {
        Greyscale -> MaterialGrey
        Red -> MaterialRed
        Orange -> MaterialOrange
        Yellow -> MaterialYellow
        Green -> MaterialGreen
        Blue -> MaterialBlue
        Purple -> MaterialPurple
        Pink -> MaterialPink
        Any -> MaterialColors
    }
}
