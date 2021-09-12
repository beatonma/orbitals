package org.beatonma.orbitalslivewallpaper.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private val Context.wallpaperDataStore: DataStore<Preferences> by preferencesDataStore(name = Settings.Wallpaper.name)
private val Context.dreamDataStore: DataStore<Preferences> by preferencesDataStore(name = Settings.Screensaver.name)

fun Context.dataStore(settings: Settings) = when(settings) {
    Settings.Wallpaper -> applicationContext.wallpaperDataStore
    Settings.Screensaver -> applicationContext.dreamDataStore
}

class OrbitalsApplication: Application() {
//    init {
//        if (BuildConfig.DEBUG) {
//            StrictMode.enableDefaults()
//        }
//    }
}
