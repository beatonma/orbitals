package org.beatonma.orbitalslivewallpaper.ui

import android.app.Application
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings

@Composable
fun App(
    viewmodel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current.applicationContext as Application,
            Settings.Wallpaper,
        )
    )
) {
    val options by viewmodel.getOptions().collectAsState(initial = Options())
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

private class SettingsViewModelFactory(
    private val application: Application,
    private val settings: Settings
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
