package org.beatonma.orbitalslivewallpaper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
            Settings.Screensaver,
        )
    ),
) {
    val options by viewmodel.options.collectAsState(initial = Options())
    val engine = rememberOrbitalsRenderEngine(options = options)
    var settingsVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = settingsVisible) {
        settingsVisible = false
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = viewmodel.settings == Settings.Screensaver,
                    onClick = { viewmodel.settings = Settings.Screensaver },
                    icon = { Icon(Icons.Default.SettingsSystemDaydream, null) },
                    label = { Text(Settings.Screensaver.name) }
                )
                NavigationBarItem(
                    selected = viewmodel.settings == Settings.Wallpaper,
                    onClick = { viewmodel.settings = Settings.Wallpaper },
                    icon = { Icon(Icons.Default.Wallpaper, null) },
                    label = { Text(Settings.Wallpaper.name) }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { insets ->
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
