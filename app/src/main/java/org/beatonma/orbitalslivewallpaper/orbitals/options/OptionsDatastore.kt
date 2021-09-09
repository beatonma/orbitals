package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.beatonma.orbitals.options.CollisionStyle
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.options.SystemGenerator
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

enum class Settings {
    Wallpaper,
    Screensaver,
    ;
}

fun getSavedOptionsSync(dataStore: DataStore<Preferences>): Options = runBlocking {
    getSavedOptions(dataStore).first()
}

@OptIn(ExperimentalTime::class)
fun getSavedOptions(dataStore: DataStore<Preferences>): Flow<Options> {
    return dataStore.data.map { preferences ->
        val physics = with (PhysicsKeys) {
            PhysicsOptions(
                maxEntities = preferences[maxEntities] ?: 25,
                systemGenerators = preferences[systemGenerators]
                    ?.map { SystemGenerator.valueOf(it) }
                    ?: listOf(
                        SystemGenerator.Gauntlet,
                        SystemGenerator.Randomized,
                        SystemGenerator.StarSystem,
                    ),
                gravityMultiplier = preferences[gravityMultiplier] ?: 1f,
                collisionStyle = preferences[collisionStyle]
                    ?.let { CollisionStyle.valueOf(it) }
                    ?: CollisionStyle.None,
                tickDelta = Duration.seconds(preferences[tickDelta] ?: 1),
            )
        }

        val colors = with(ColorKeys) {
            ColorOptions(
                background = preferences[background] ?: Color.BLACK,
                foregroundAlpha = preferences[foregroundAlpha] ?: 1f,
                bodies = preferences[bodies]
                    ?.map { ObjectColors.valueOf(it) }
                    ?: listOf(
                        ObjectColors.Red,
                        ObjectColors.Purple,
                    ),
            )
        }

        val visuals = with(VisualKeys) {
            VisualOptions(
                colorOptions = colors,
                showTraceLines = preferences[showTraceLines] ?: false,
                traceLineLength = preferences[traceLineLength] ?: 50,
                showAcceleration = preferences[showAcceleration] ?: false,
                drawStyle = preferences[drawStyle]
                    ?.let { DrawStyle.valueOf(it) }
                    ?: DrawStyle.Solid,
                strokeWidth = (preferences[strokeWidth] ?: 4f).dp
            )
        }

        Options(physics, visuals)
    }
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

object VisualKeys {
    val focusCenterOfMass = booleanPreferencesKey("focus_center_of_mass")
    val showTraceLines = booleanPreferencesKey("show_path_history")
    val traceLineLength = intPreferencesKey("path_history_length")
    val showAcceleration = booleanPreferencesKey("show_acceleration")
    val drawStyle = stringPreferencesKey("draw_style")
    val strokeWidth = floatPreferencesKey("stroke_width")
}

object PhysicsKeys {
    val maxEntities = intPreferencesKey("max_entities")
    val systemGenerators = stringSetPreferencesKey("system_generators")
    val gravityMultiplier = floatPreferencesKey("gravity_multiplier")
    val collisionStyle = stringPreferencesKey("collision_style")
    val tickDelta = intPreferencesKey("tick_delta")
}

object ColorKeys {
    val background = intPreferencesKey("background_color")
    val bodies = stringSetPreferencesKey("body_colors")
    val foregroundAlpha = floatPreferencesKey("foreground_alpha")
}
