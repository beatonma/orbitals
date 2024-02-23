package org.beatonma.orbitals.render.compose

import org.beatonma.orbitals.render.color.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import androidx.compose.ui.graphics.Color as ComposeColor

class ComposeColorTest {
    @Test
    fun testColorToComposeColor() {
        assertEquals(ComposeColor.Red, Color(0xff0000).toComposeColor())
        assertEquals(ComposeColor.Green, Color(0x00ff00).toComposeColor())
        assertEquals(ComposeColor.Blue, Color(0x0000ff).toComposeColor())

        assertEquals(ComposeColor(0xaa11223344), Color(0xaa11223344).toComposeColor())
    }
}
