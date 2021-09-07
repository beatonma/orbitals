package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Color
import org.beatonma.orbitalslivewallpaper.color.*

data class ColorOptions(
    val backgroundColor: Int = Color.BLACK,
    val objectColors: List<ObjectColors> = listOf(
        ObjectColors.Greyscale
    ),
    val foregroundAlpha: Float = 1f,
)

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
