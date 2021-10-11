package org.beatonma.orbitalslivewallpaper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey

enum class Settings {
    Wallpaper,
    Screensaver,
    ;
}

fun getSavedOptionsSync(dataStore: DataStore<Preferences>): Options = runBlocking {
    getSavedOptions(dataStore).first()
}

private operator fun <T> Preferences.get(key: Key<T>) = get(key.asPreferenceKey)

private class AndroidOptionsStore(
    private val preferences: Preferences
) : OptionsStore {
    override operator fun <T> get(key: Key<T>): T? = preferences[key]
}

fun getSavedOptions(dataStore: DataStore<Preferences>): Flow<Options> {
    return dataStore.data.map {
        AndroidOptionsStore(it).loadOptions()
    }
}

fun getSavedColors(dataStore: DataStore<Preferences>): Flow<ColorOptions> {
    return dataStore.data.map { AndroidOptionsStore(it).loadColors() }
}

suspend fun <T> updateOption(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<T>,
    value: T
) {
    dataStore.edit { preferences ->
        preferences[key] = value
    }
}


val <T> Key<T>.asPreferenceKey: Preferences.Key<T>
    get() = when (this) {
        is StringKey<*> -> this.asPreferenceKey
        is StringSetKey<*> -> this.asPreferenceKey
        is IntKey -> this.asPreferenceKey
        is FloatKey -> this.asPreferenceKey
        is BooleanKey -> this.asPreferenceKey
    } as Preferences.Key<T>

val StringKey<*>.asPreferenceKey: Preferences.Key<String> get() = stringPreferencesKey(key)
val StringSetKey<*>.asPreferenceKey: Preferences.Key<Set<String>>
    get() = stringSetPreferencesKey(
        key
    )
val IntKey.asPreferenceKey: Preferences.Key<Int> get() = intPreferencesKey(key)
val FloatKey.asPreferenceKey: Preferences.Key<Float> get() = floatPreferencesKey(key)
val BooleanKey.asPreferenceKey: Preferences.Key<Boolean> get() = booleanPreferencesKey(key)
