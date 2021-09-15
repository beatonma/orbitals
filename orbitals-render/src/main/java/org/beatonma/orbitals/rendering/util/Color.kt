package org.beatonma.orbitals.rendering.util

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color


fun Int.toComposeColor() = Color(
    AndroidColor.red(this),
    AndroidColor.green(this),
    AndroidColor.blue(this),
    0xff,
)
