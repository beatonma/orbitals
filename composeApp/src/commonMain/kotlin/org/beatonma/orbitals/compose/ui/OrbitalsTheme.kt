package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.compose.ui.theme.blue.BlueColorScheme
import org.beatonma.orbitals.compose.ui.theme.green.GreenColorScheme
import org.beatonma.orbitals.compose.ui.theme.greyscale.GreyColorScheme
import org.beatonma.orbitals.compose.ui.theme.orange.OrangeColorScheme
import org.beatonma.orbitals.compose.ui.theme.pink.PinkColorScheme
import org.beatonma.orbitals.compose.ui.theme.purple.PurpleColorScheme
import org.beatonma.orbitals.compose.ui.theme.red.RedColorScheme
import org.beatonma.orbitals.compose.ui.theme.yellow.YellowColorScheme
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.ObjectColors

val ColorScheme.settingsScrim @Composable get() = surface.copy(alpha = .7f)

@Composable
fun OrbitalsTheme(
    options: ColorOptions,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = chooseColorScheme(options, isDark),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp),
        )
    ) {
        Surface(
            Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
private fun chooseColorScheme(options: ColorOptions, isDark: Boolean): ColorScheme {
    val color by remember(options.bodies) { mutableStateOf(options.bodies.random()) }

    return schemeForColor(color, isDark)
}


@Composable
private fun schemeForColor(color: ObjectColors, isDark: Boolean): ColorScheme =
    when (color) {
        ObjectColors.Greyscale -> GreyColorScheme(isDark)
        ObjectColors.Red -> RedColorScheme(isDark)
        ObjectColors.Orange -> OrangeColorScheme(isDark)
        ObjectColors.Yellow -> YellowColorScheme(isDark)
        ObjectColors.Green -> GreenColorScheme(isDark)
        ObjectColors.Blue -> BlueColorScheme(isDark)
        ObjectColors.Purple -> PurpleColorScheme(isDark)
        ObjectColors.Pink -> PinkColorScheme(isDark)
        ObjectColors.Any -> schemeForColor(
            ObjectColors.entries.filterNot { it == ObjectColors.Any }.random(),
            isDark
        )
    }
