package org.beatonma.orbitalslivewallpaper.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.options.Settings
import org.beatonma.orbitalslivewallpaper.orbitals.options.getSavedOptions
import org.beatonma.orbitalslivewallpaper.orbitals.options.updateOption


class SettingsViewModel(
    context: Context,
    settings: Settings,
) : AndroidViewModel(context.applicationContext as Application) {
    private val prefsDatastore: DataStore<Preferences> = context.dataStore(settings)

    fun getOptions(): Flow<Options> = getSavedOptions(prefsDatastore)

    fun <T> updateOption(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            updateOption(
                prefsDatastore,
                key,
                value,
            )
        }
    }
}
