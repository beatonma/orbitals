package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.view.KeyEvent
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.interaction.Key


fun Color.toAndroidColor() = toRgbInt() or 0xff000000.toInt()

internal fun Int.toKey(): Key? = when (this) {
    KeyEvent.KEYCODE_A -> Key.A
    KeyEvent.KEYCODE_B -> Key.B
    KeyEvent.KEYCODE_C -> Key.C
    KeyEvent.KEYCODE_D -> Key.D
    KeyEvent.KEYCODE_E -> Key.E
    KeyEvent.KEYCODE_F -> Key.F
    KeyEvent.KEYCODE_G -> Key.G
    KeyEvent.KEYCODE_H -> Key.H
    KeyEvent.KEYCODE_I -> Key.I
    KeyEvent.KEYCODE_J -> Key.J
    KeyEvent.KEYCODE_K -> Key.K
    KeyEvent.KEYCODE_L -> Key.L
    KeyEvent.KEYCODE_M -> Key.M
    KeyEvent.KEYCODE_N -> Key.N
    KeyEvent.KEYCODE_O -> Key.O
    KeyEvent.KEYCODE_P -> Key.P
    KeyEvent.KEYCODE_Q -> Key.Q
    KeyEvent.KEYCODE_R -> Key.R
    KeyEvent.KEYCODE_S -> Key.S
    KeyEvent.KEYCODE_T -> Key.T
    KeyEvent.KEYCODE_U -> Key.U
    KeyEvent.KEYCODE_V -> Key.V
    KeyEvent.KEYCODE_W -> Key.W
    KeyEvent.KEYCODE_X -> Key.X
    KeyEvent.KEYCODE_Y -> Key.Y
    KeyEvent.KEYCODE_Z -> Key.Z
    KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> Key.One
    KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> Key.Two
    KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> Key.Three
    KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> Key.Four
    KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> Key.Five
    KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> Key.Six
    KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> Key.Seven
    KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> Key.Eight
    KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> Key.Nine
    KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> Key.Zero
    KeyEvent.KEYCODE_ESCAPE -> Key.Escape
    KeyEvent.KEYCODE_GRAVE -> Key.Grave
    KeyEvent.KEYCODE_FORWARD_DEL -> Key.Delete
    KeyEvent.KEYCODE_DEL -> Key.Backspace
    KeyEvent.KEYCODE_INSERT -> Key.Insert
    KeyEvent.KEYCODE_SPACE -> Key.Spacebar
    KeyEvent.KEYCODE_LEFT_BRACKET -> Key.LeftBracket
    KeyEvent.KEYCODE_RIGHT_BRACKET -> Key.RightBracket
    KeyEvent.KEYCODE_NUMPAD_ADD -> Key.Add
    KeyEvent.KEYCODE_NUMPAD_SUBTRACT -> Key.Subtract
    else -> null
}
