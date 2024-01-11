package org.beatonma.orbitalslivewallpaper.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.beatonma.orbitals.compose.ui.OrbitalsTheme
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.getSavedColors

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

    OrbitalsTheme(colorsOptions, isDark = isDark, content = content)
}
