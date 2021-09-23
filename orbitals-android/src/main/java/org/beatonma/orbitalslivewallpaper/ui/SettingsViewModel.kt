package org.beatonma.orbitalslivewallpaper.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.getSavedOptions


class SettingsViewModel(
    context: Context,
    settings: Settings,
) : AndroidViewModel(context.applicationContext as Application) {
    private val prefsDatastore: DataStore<Preferences> = context.dataStore(settings)

    fun getOptions(): Flow<Options> = getSavedOptions(prefsDatastore)

    fun <T> updateOption(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            org.beatonma.orbitalslivewallpaper.updateOption(
                prefsDatastore,
                key,
                value,
            )
        }
    }
}
