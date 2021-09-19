package org.beatonma.orbitalslivewallpaper.app

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
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.warn
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
        val physics = loadPhysicsOptions(preferences)
        val colors = loadColors(preferences)
        val visuals = loadVisualOptions(preferences, colors)

        Options(physics, visuals)
    }
}

fun getSavedColors(dataStore: DataStore<Preferences>): Flow<ColorOptions> {
    return dataStore.data.map(::loadColors)
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
    val renderLayers = stringSetPreferencesKey("render_layers")
    val focusCenterOfMass = booleanPreferencesKey("focus_center_of_mass")
    val traceLineLength = intPreferencesKey("path_history_length")
    val drawStyle = stringPreferencesKey("draw_style")
    val strokeWidth = floatPreferencesKey("stroke_width")
}

object PhysicsKeys {
    val autoAddBodies = booleanPreferencesKey("auto_add_bodies")
    val maxFixedBodyAgeMinutes = intPreferencesKey("max_fixedbody_age_minutes")
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

private fun loadColors(preferences: Preferences): ColorOptions =
    with(ColorKeys) {
        ColorOptions(
            background = preferences[background] ?: 0x000000,
            foregroundAlpha = preferences[foregroundAlpha] ?: 1f,
            bodies = preferences[bodies]
                ?.map {
                    safeValueOf(
                        it,
                        default = ObjectColors.Greyscale
                    )
                }
                ?.toSet()
                ?: setOf(
                    ObjectColors.Red,
                    ObjectColors.Purple,
                ),
        )
    }

private fun loadVisualOptions(
    preferences: Preferences,
    colors: ColorOptions = loadColors(preferences),
): VisualOptions = with(VisualKeys) {
    VisualOptions(
        renderLayers = preferences[renderLayers]
            ?.map { safeValueOf(it, default = RenderLayer.Default) }
            ?.toSet()
            ?: setOf(RenderLayer.Default),
        colorOptions = colors,
        traceLineLength = preferences[traceLineLength] ?: 50,
        drawStyle = preferences[drawStyle]
            ?.let { safeValueOf(it, default = DrawStyle.Solid) }
            ?: DrawStyle.Solid,
        strokeWidth = preferences[strokeWidth] ?: 4f,
    )
}

@OptIn(ExperimentalTime::class)
private fun loadPhysicsOptions(
    preferences: Preferences,
): PhysicsOptions = with(PhysicsKeys) {
    PhysicsOptions(
        autoAddBodies = preferences[autoAddBodies] ?: true,
        maxEntities = preferences[maxEntities] ?: 25,
        maxFixedBodyAgeMinutes = Duration.minutes(preferences[maxFixedBodyAgeMinutes] ?: 1),
        systemGenerators = preferences[systemGenerators]
            ?.map { safeValueOf(it, default = SystemGenerator.StarSystem) }
            ?.toSet()
            ?: setOf(
                SystemGenerator.Gauntlet,
                SystemGenerator.Randomized,
                SystemGenerator.StarSystem,
            ),
        gravityMultiplier = preferences[gravityMultiplier] ?: 1f,
        collisionStyle = preferences[collisionStyle]
            ?.let { safeValueOf(it, CollisionStyle.None) }
            ?: CollisionStyle.None,
        tickDelta = Duration.seconds(preferences[tickDelta] ?: 1),
    )
}

private inline fun <reified E : Enum<E>> safeValueOf(value: String, default: E): E =
    try {
        java.lang.Enum.valueOf(E::class.java, value)
    } catch (e: IllegalArgumentException) {
        warn(e)
        default
    }
