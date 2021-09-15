package org.beatonma.orbitals.options

import android.graphics.Color
import org.beatonma.orbitals.color.MaterialBlue
import org.beatonma.orbitals.color.MaterialColors
import org.beatonma.orbitals.color.MaterialGreen
import org.beatonma.orbitals.color.MaterialGrey
import org.beatonma.orbitals.color.MaterialOrange
import org.beatonma.orbitals.color.MaterialPink
import org.beatonma.orbitals.color.MaterialPurple
import org.beatonma.orbitals.color.MaterialRed
import org.beatonma.orbitals.color.MaterialYellow

data class ColorOptions(
    val background: Int = Color.BLACK,
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
