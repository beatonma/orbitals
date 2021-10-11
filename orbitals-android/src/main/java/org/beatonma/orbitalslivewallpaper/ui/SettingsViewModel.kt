package org.beatonma.orbitalslivewallpaper.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.asPreferenceKey
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.getSavedOptions


class SettingsViewModel(
    context: Context,
    settings: Settings,
) : AndroidViewModel(context.applicationContext as Application), OptionPersistence {
    private val prefsDatastore: DataStore<Preferences> = context.dataStore(settings)

    fun getOptions(): Flow<Options> = getSavedOptions(prefsDatastore)

    override fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E) {
        savePreference(key.asPreferenceKey, value.name)
    }

    override fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>) {
        savePreference(key.asPreferenceKey, value.map { it.name }.toSet())
    }

    override fun updateOption(key: IntKey, value: Int) {
        savePreference(key.asPreferenceKey, value)
    }

    override fun updateOption(key: FloatKey, value: Float) {
        savePreference(key.asPreferenceKey, value)
    }

    override fun updateOption(key: BooleanKey, value: Boolean) {
        savePreference(key.asPreferenceKey, value)
    }

    private fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            org.beatonma.orbitalslivewallpaper.updateOption(
                prefsDatastore,
                key,
                value,
            )
        }
    }
}
