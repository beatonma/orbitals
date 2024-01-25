package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.android.AndroidCanvasDelegate
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.ui.SettingsViewModel
import org.beatonma.orbitalslivewallpaper.ui.orbitals.OrbitalsEngineViewModel.OnRenderEngineReady


class OrbitalsEngineViewModel(
    context: Context,
    settings: Settings,
) : SettingsViewModel(context, settings) {
    var renderEngine: OrbitalsRenderEngine<Canvas>? = null
        private set
    private var onRenderEngineReady: OnRenderEngineReady? = null

    init {
        viewModelScope.launch {
            options.collectLatest { options ->
                renderEngine?.let { it.options = options } ?: initRenderEngine(options)
            }
        }
    }

    fun onRenderEngineReady(block: (OrbitalsRenderEngine<Canvas>) -> Unit) {
        renderEngine?.let(block) ?: run {
            onRenderEngineReady = OnRenderEngineReady(block)
        }
    }

    fun recycle() {
        onRenderEngineReady = null
        renderEngine?.recycle()
    }

    private fun initRenderEngine(options: Options) {
        renderEngine = OrbitalsRenderEngine(AndroidCanvasDelegate, options).also {
            onRenderEngineReady?.invoke(it)
        }
        onRenderEngineReady = null
    }

    companion object {
        fun factory(context: Context, settings: Settings): ViewModelProvider.Factory =
            OrbitalsViewModelFactory(context.applicationContext as Application, settings)

        private class OrbitalsViewModelFactory(
            private val application: Application,
            private val settings: Settings
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OrbitalsEngineViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return OrbitalsEngineViewModel(application, settings) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun interface OnRenderEngineReady {
        operator fun invoke(renderEngine: OrbitalsRenderEngine<Canvas>)
    }
}
