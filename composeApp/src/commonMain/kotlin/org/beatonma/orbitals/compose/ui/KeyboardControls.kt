package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.focusable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.interaction.Key
import org.beatonma.orbitals.render.interaction.OrbitalsKeyboardHandler
import org.beatonma.orbitals.render.options.OptionPersistence
import androidx.compose.ui.input.key.Key as ComposeKey


internal fun Modifier.keyboardHandler(
    engine: OrbitalsRenderEngine<*>,
    persistence: OptionPersistence,
    onToggleSettingsVisible: () -> Unit,
): Modifier = composed {
    val focusRequester = remember(::FocusRequester)
    val keyboard = remember { OrbitalsKeyboardHandler(engine, persistence) }

    LaunchedEffect(onToggleSettingsVisible) { focusRequester.requestFocus() }

    focusRequester(focusRequester)
        .focusable()
        .onKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

            when (val key = event.key) {
                ComposeKey.Escape, ComposeKey.Grave -> {
                    onToggleSettingsVisible()
                    true
                }

                else -> key.toOrbitalsKey()?.let(keyboard::onKeyDown) ?: false
            }

        }
}

private fun ComposeKey.toOrbitalsKey(): Key? = when (this) {
    ComposeKey.A -> Key.A
    ComposeKey.B -> Key.B
    ComposeKey.C -> Key.C
    ComposeKey.D -> Key.D
    ComposeKey.E -> Key.E
    ComposeKey.F -> Key.F
    ComposeKey.G -> Key.G
    ComposeKey.H -> Key.H
    ComposeKey.I -> Key.I
    ComposeKey.J -> Key.J
    ComposeKey.K -> Key.K
    ComposeKey.L -> Key.L
    ComposeKey.M -> Key.M
    ComposeKey.N -> Key.N
    ComposeKey.O -> Key.O
    ComposeKey.P -> Key.P
    ComposeKey.Q -> Key.Q
    ComposeKey.R -> Key.R
    ComposeKey.S -> Key.S
    ComposeKey.T -> Key.T
    ComposeKey.U -> Key.U
    ComposeKey.V -> Key.V
    ComposeKey.W -> Key.W
    ComposeKey.X -> Key.X
    ComposeKey.Y -> Key.Y
    ComposeKey.Z -> Key.Z
    ComposeKey.One, ComposeKey.NumPad1 -> Key.One
    ComposeKey.Two, ComposeKey.NumPad2 -> Key.Two
    ComposeKey.Three, ComposeKey.NumPad3 -> Key.Three
    ComposeKey.Four, ComposeKey.NumPad4 -> Key.Four
    ComposeKey.Five, ComposeKey.NumPad5 -> Key.Five
    ComposeKey.Six, ComposeKey.NumPad6 -> Key.Six
    ComposeKey.Seven, ComposeKey.NumPad7 -> Key.Seven
    ComposeKey.Eight, ComposeKey.NumPad8 -> Key.Eight
    ComposeKey.Nine, ComposeKey.NumPad9 -> Key.Nine
    ComposeKey.Zero, ComposeKey.NumPad0 -> Key.Zero
    ComposeKey.Escape -> Key.Escape
    ComposeKey.Grave -> Key.Grave
    ComposeKey.Delete -> Key.Delete
    ComposeKey.Backspace -> Key.Backspace
    ComposeKey.Insert -> Key.Insert
    ComposeKey.Spacebar -> Key.Spacebar
    ComposeKey.LeftBracket -> Key.LeftBracket
    ComposeKey.RightBracket -> Key.RightBracket
    ComposeKey.NumPadAdd -> Key.Add
    ComposeKey.NumPadSubtract -> Key.Subtract
    else -> null
}
