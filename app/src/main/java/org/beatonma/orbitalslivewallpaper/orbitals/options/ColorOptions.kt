package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Color
import org.beatonma.orbitalslivewallpaper.color.MaterialBlue
import org.beatonma.orbitalslivewallpaper.color.MaterialColors
import org.beatonma.orbitalslivewallpaper.color.MaterialGreen
import org.beatonma.orbitalslivewallpaper.color.MaterialGrey
import org.beatonma.orbitalslivewallpaper.color.MaterialOrange
import org.beatonma.orbitalslivewallpaper.color.MaterialPink
import org.beatonma.orbitalslivewallpaper.color.MaterialPurple
import org.beatonma.orbitalslivewallpaper.color.MaterialRed
import org.beatonma.orbitalslivewallpaper.color.MaterialYellow

data class ColorOptions(
    val background: Int = Color.BLACK,
    val bodies: List<ObjectColors> = listOf(
        ObjectColors.Greyscale,
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
