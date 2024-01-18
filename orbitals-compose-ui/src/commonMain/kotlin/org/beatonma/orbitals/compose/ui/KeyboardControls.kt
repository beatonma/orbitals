package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.focusable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.PhysicsKeys
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualKeys

private const val DensityStepSize = .05f


internal fun Modifier.keyboardHandler(
    engine: OrbitalsRenderEngine<*>,
    options: Options,
    persistence: OptionPersistence,
    onToggleSettingsVisible: () -> Unit,
): Modifier = composed {
    val focusRequester = remember(::FocusRequester)

    LaunchedEffect(onToggleSettingsVisible) { focusRequester.requestFocus() }

    fun toggleLayer(layer: RenderLayer) = hotkey {
        persistence.updateOption(
            VisualKeys.RenderLayers,
            options.visualOptions.renderLayers.toggle(layer)
        )
    }

    fun setCollisionStyle(style: CollisionStyle) = hotkey {
        persistence.updateOption(PhysicsKeys.CollisionStyle, style)
    }

    fun increaseSize() = hotkey {
        persistence.updateOption(
            PhysicsKeys.Density,
            (options.physics.bodyDensity.value - DensityStepSize).coerceAtLeast(0.01f)
        )
    }

    fun decreaseSize() = hotkey {
        persistence.updateOption(
            PhysicsKeys.Density,
            options.physics.bodyDensity.value + DensityStepSize
        )
    }

    focusRequester(focusRequester)
        .focusable()
        .onKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

            when (event.key) {
                Key.Escape, Key.Grave -> hotkey(onToggleSettingsVisible)

                Key.Delete, Key.Backspace -> hotkey(engine::clear)
                Key.Insert, Key.Spacebar -> hotkey(engine::addBodies)

                // Render lays
                Key.One, Key.NumPad1 -> toggleLayer(RenderLayer.entries[0])
                Key.Two, Key.NumPad2 -> toggleLayer(RenderLayer.entries[1])
                Key.Three, Key.NumPad3 -> toggleLayer(RenderLayer.entries[2])

                // Collisions
                Key.Q -> setCollisionStyle(CollisionStyle.entries[0])
                Key.W -> setCollisionStyle(CollisionStyle.entries[1])
                Key.E -> setCollisionStyle(CollisionStyle.entries[2])
                Key.R -> setCollisionStyle(CollisionStyle.entries[3])
                Key.T -> setCollisionStyle(CollisionStyle.entries[4])

                // Density (-> rendered size)
                Key.LeftBracket, Key.NumPadAdd -> increaseSize()
                Key.RightBracket, Key.NumPadSubtract -> decreaseSize()

                else -> false
            }
        }
}

private inline fun hotkey(block: () -> Unit): Boolean = block().let { true }
private inline fun hotkey(condition: Boolean, block: () -> Unit): Boolean =
    condition && block().let { true }

private fun <T> Set<T>.toggle(value: T): Set<T> = when (value) {
    in this -> this - value
    else -> this + value
}
