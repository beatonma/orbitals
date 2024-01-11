package org.beatonma.orbitalslivewallpaper.ui

import android.app.Application
import android.view.KeyEvent
import android.view.View
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings

@Composable
fun SettingsUI(
    viewmodel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current.applicationContext as Application,
            Settings.Wallpaper,
        )
    )
) {
    val view = LocalView.current

    val options by viewmodel.getOptions().collectAsState(initial = Options())
    val engine = rememberOrbitalsRenderEngine(options = options)

    DisposableEffect(view) {
        val keyListener: View.OnKeyListener = View.OnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_SPACE -> {
                    engine.addBodies()
                    true
                }

                else -> false
            }
        }
        view.setOnKeyListener(keyListener)

        onDispose {
            view.setOnKeyListener(null)
        }
    }

    Scaffold { insets ->
        EditableOrbitals(
            options,
            persistence = viewmodel,
            contentPadding = insets,
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
