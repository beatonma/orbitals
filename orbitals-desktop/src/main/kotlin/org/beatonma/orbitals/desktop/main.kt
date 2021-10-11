package org.beatonma.orbitals.desktop

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.beatonma.orbitals.compose.ui.SettingsUi
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey

fun main() = application {
    val persistence = remember { PersistentOptions }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Orbitals"
    ) {
        var optionsVisible by remember { mutableStateOf(false) }

        Box {
            Crossfade(optionsVisible) { inMenu ->
                when {
                    inMenu -> {
                        SettingsUi(persistence.options, persistence)
                    }
                    else -> {
                        Orbitals(
                            persistence.options,
                            Modifier
                                .background(Color.DarkGray)
                                .fillMaxSize(),
                        )
                    }
                }
            }

            Icon(
                Icons.Default.Menu,
                contentDescription = "",
                modifier = Modifier
                    .clickable { optionsVisible = !optionsVisible },
                tint = remember { persistence.options.visualOptions.colorOptions.colorForBody.toComposeColor() },
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
