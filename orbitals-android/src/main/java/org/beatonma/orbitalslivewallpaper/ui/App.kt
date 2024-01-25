package org.beatonma.orbitalslivewallpaper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings

@Composable
fun App(
    viewmodel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.factory(
            LocalContext.current.applicationContext,
            Settings.Wallpaper,
        )
    )
) {
    val options by viewmodel.options.collectAsState(initial = Options())
    val engine = rememberOrbitalsRenderEngine(options = options)
    var settingsVisible by remember { mutableStateOf(false) }

    BackHandler(enabled = settingsVisible) {
        settingsVisible = false
    }

    Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { insets ->
        EditableOrbitals(
            settingsEnabled = true,
            onSettingsEnabledChange = {},
            settingsVisible = settingsVisible,
            onSettingsVisibleChange = { settingsVisible = it },
            options = options,
            persistence = viewmodel,
            insets = insets,
            engine = engine,
        )
    }
}
