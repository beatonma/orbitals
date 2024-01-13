package org.beatonma.orbitals.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.compose.ui.OrbitalsTheme
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey

fun main() = application {
    val persistence = PersistentOptions
    val engine = rememberOrbitalsRenderEngine(persistence.options)

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1600.dp, 1200.dp),
        ),
        title = "Orbitals"
    ) {
        OrbitalsTheme(
            persistence.options.visualOptions.colorOptions,
            isDark = true,
        ) {
            EditableOrbitals(
                persistence.options,
                persistence,
                engine = engine,
            )
        }
    }
}

private object PersistentOptions : OptionPersistence, OptionsStore {
    var options by mutableStateOf(Options())
    private val optionMap = mutableMapOf<Key<*>, Any?>()

    override fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E) {
        optionMap[key] = value.name
        options = loadOptions()
    }

    override fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>) {
        optionMap[key] = value.map { it.name }.toSet()
        options = loadOptions()
    }

    override fun updateOption(key: IntKey, value: Int) {
        optionMap[key] = value
        options = loadOptions()
    }

    override fun updateOption(key: ColorKey, value: Color) {
        optionMap[key] = value
        options = loadOptions()
    }

    override fun updateOption(key: FloatKey, value: Float) {
        optionMap[key] = value
        options = loadOptions()
    }

    override fun updateOption(key: BooleanKey, value: Boolean) {
        optionMap[key] = value
        options = loadOptions()
    }

    override fun <T> get(key: Key<T>): T? {
        return optionMap[key] as? T
    }
}
