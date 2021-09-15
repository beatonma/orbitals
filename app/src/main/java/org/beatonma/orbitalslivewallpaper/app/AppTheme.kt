package org.beatonma.orbitalslivewallpaper.app

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import org.beatonma.orbitalslivewallpaper.orbitals.options.ColorOptions
import org.beatonma.orbitalslivewallpaper.orbitals.render.util.toComposeColor

@Composable
fun AppTheme(
    isDark: Boolean = isSystemInDarkTheme() || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q,
    content: @Composable () -> Unit,
) {
    val colorsOptions by getSavedColors(
        LocalContext.current.dataStore(Settings.Wallpaper)
    ).collectAsState(
        initial = ColorOptions()
    )

    MaterialTheme(
        colors = if (isDark) {
            darkColors(
                colorsOptions.bodies.random().colors(),
                colorsOptions.bodies.random().colors()
            )
        } else {
            lightColors(
                colorsOptions.bodies.random().colors(),
                colorsOptions.bodies.random().colors()
            )
        },
        shapes = Shapes(
            small = CutCornerShape(4.dp),
            medium = CutCornerShape(8.dp),
            large = CutCornerShape(8.dp),
        )
    ) {
        ProvideWindowInsets {
            Surface(
                Modifier.fillMaxSize(),
                content = content
            )
        }
    }
}


private fun lightColors(
    primaryColors: Array<Int>,
    secondaryColors: Array<Int>,
) = androidx.compose.material.lightColors(
    primary = primaryColors[primaryColors.size / 2].toComposeColor(),
    primaryVariant = primaryColors[primaryColors.size / 4].toComposeColor(),
    secondary = secondaryColors[secondaryColors.size / 2].toComposeColor(),
    secondaryVariant = secondaryColors[secondaryColors.size / 4].toComposeColor(),
)

private fun darkColors(
    primaryColors: Array<Int>,
    secondaryColors: Array<Int>,
) = androidx.compose.material.darkColors(
    primary = primaryColors[primaryColors.size / 2].toComposeColor(),
    primaryVariant = primaryColors[primaryColors.size / 4].toComposeColor(),
    secondary = secondaryColors[secondaryColors.size / 2].toComposeColor(),
    secondaryVariant = secondaryColors[secondaryColors.size / 4].toComposeColor(),
)
