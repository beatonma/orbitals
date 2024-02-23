package org.beatonma.orbitals.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.color.toColor
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey
import java.util.prefs.Preferences

private const val StringSetSeparator = "_,+"


class PersistentOptions : OptionPersistence, OptionsStore {
    private val prefs = Preferences.userRoot().node("beatonma/orbitals")
    var options by mutableStateOf(loadOptions())
        private set

    override fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E) {
        prefs.put(key.key, value.name)
        options = loadOptions()
    }

    override fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>) {
        prefs.put(key.key, value.joinToString(StringSetSeparator))
        options = loadOptions()
    }

    override fun updateOption(key: IntKey, value: Int) {
        prefs.putInt(key.key, value)
        options = loadOptions()
    }

    override fun updateOption(key: ColorKey, value: Color) {
        prefs.put(key.key, value.toStringRgb())
        options = loadOptions()
    }

    override fun updateOption(key: FloatKey, value: Float) {
        prefs.putFloat(key.key, value)
        options = loadOptions()
    }

    override fun updateOption(key: BooleanKey, value: Boolean) {
        prefs.putBoolean(key.key, value)
        options = loadOptions()
    }

    override fun <T> get(key: Key<T>): T? {
        if (!prefs.keys().contains(key.key)) return null

        @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
        return when (key) {
            is BooleanKey -> prefs.getBoolean(key.key, false)
            is ColorKey -> prefs.get(key.key, "#000").toColor()
            is FloatKey -> prefs.getFloat(key.key, 0f)
            is IntKey -> prefs.getInt(key.key, 0)
            is StringKey<*> -> prefs.get(key.key, "")
            is StringSetKey<*> -> prefs.get(key.key, "").split(StringSetSeparator).toSet()
        } as T
    }
}
