package org.beatonma.orbitalslivewallpaper.ui

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.compose.ui.SettingsUi
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsUI(
    viewmodel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current.applicationContext as Application,
            Settings.Wallpaper,
        )
    )
) {
    val options by viewmodel.getOptions().collectAsState(initial = Options())

    SettingsUi(options, viewmodel)
}

private class SettingsViewModelFactory(
    private val application: Application,
    private val settings: Settings
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
