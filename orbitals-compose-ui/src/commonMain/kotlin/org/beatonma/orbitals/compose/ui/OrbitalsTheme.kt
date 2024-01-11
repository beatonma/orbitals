package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.render.options.ColorOptions

@Composable
fun OrbitalsTheme(
    options: ColorOptions,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isDark) {
            darkColors(
                options.bodies.last().colors(),
                options.bodies.first().colors()
            )
        } else {
            lightColors(
                options.bodies.last().colors(),
                options.bodies.first().colors()
            )
        },
        shapes = Shapes(
            small = CutCornerShape(4.dp),
            medium = CutCornerShape(8.dp),
            large = CutCornerShape(8.dp),
        )
    ) {
        Surface(
            Modifier.fillMaxSize(),
            content = content
        )
    }
}


private fun lightColors(
    primaryColors: Array<Int>,
    secondaryColors: Array<Int>,
) = androidx.compose.material.lightColors(
    primary = Color(primaryColors[primaryColors.size / 2]),
    primaryVariant = Color(primaryColors[primaryColors.size / 4]),
    secondary = Color(secondaryColors[secondaryColors.size / 2]),
    secondaryVariant = Color(secondaryColors[secondaryColors.size / 4]),
)

private fun darkColors(
    primaryColors: Array<Int>,
    secondaryColors: Array<Int>,
) = androidx.compose.material.darkColors(
    primary = Color(primaryColors[primaryColors.size / 2]),
    primaryVariant = Color(primaryColors[primaryColors.size / 4]),
    secondary = Color(secondaryColors[secondaryColors.size / 2]),
    secondaryVariant = Color(secondaryColors[secondaryColors.size / 4]),
)
