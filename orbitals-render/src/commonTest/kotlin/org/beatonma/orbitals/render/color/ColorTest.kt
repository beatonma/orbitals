package org.beatonma.orbitals.render.color

import org.beatonma.orbitals.test.shouldbe
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun testIntComponents() {
        Color(0x11223344L).run {
            assertEquals(17, alpha)
            assertEquals(34, red)
            assertEquals(51, green)
            assertEquals(68, blue)
        }
    }

    @Test
    fun testWithOpacity() {
        Color(0x4aff0000L).run {
            assertEquals(Color(0x00ff0000L), withOpacity(0f))
            assertEquals(Color(0x7fff0000L), withOpacity(.5f))
            assertEquals(Color(0xffff0000L), withOpacity(1f))
        }
    }

    @Test
    fun testRgb() {
        assertContentEquals(Color(0x11223344L).rgb(), arrayOf(0x22, 0x33, 0x44))
    }

    @Test
    fun testArgb() {
        assertContentEquals(Color(0xccddeeffL).argb(), arrayOf(0xcc, 0xdd, 0xee, 0xff))
    }

    @Test
    fun testRgba() {
        assertContentEquals(Color(0xaa75dd9bL).rgba(), arrayOf(0x75, 0xdd, 0x9b, 0xaa))
    }

    @Test
    fun testToRgbInt() {
        assertEquals(Color(0xaabbccddL).toRgbInt(), 0xbbccdd)
    }

    @Test
    fun testFromArgb() {
        assertEquals(
            Color(0x11223344L),
            Color.fromArgb(0x11U, 0x22U, 0x33U, 0x44U)
        )
    }

    @Test
    fun testIntConstructor() {
        Color(0xff0000).run {
            alpha shouldbe 0xff
            red shouldbe 0xff
            green shouldbe 0
            blue shouldbe 0
        }
    }

    @Test
    fun testLongConstructor() {
        Color(0xffff0000).run {
            alpha shouldbe 0xff
            red shouldbe 0xff
            green shouldbe 0
            blue shouldbe 0
        }
    }

    @Test
    fun testLosslessConversion() {
        Color.fromArgb(0x11U, 0x22U, 0x33U, 0x44U).argb() shouldbe arrayOf(0x11, 0x22, 0x33, 0x44)
        Color.fromArgb(0x11U, 0x22U, 0x33U, 0x44U).value.let(::Color).argb() shouldbe arrayOf(
            0x11,
            0x22,
            0x33,
            0x44
        )
        Color.fromArgb(0xffU, 0x22U, 0x33U, 0x44U).toRgbInt().let(::Color).rgb() shouldbe arrayOf(
            0x22,
            0x33,
            0x44
        )
        Color(0xffaabb).toRgbInt() shouldbe 0xffaabb
        repeat(100) {
            val n = Random.nextInt(0, 0xffffff)
            Color(n).toRgbInt() shouldbe n
        }
    }
}
