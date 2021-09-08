package org.beatonma.orbitalslivewallpaper.test

import kotlin.test.assertEquals
import kotlin.test.assertTrue


infix fun <T> T?.shouldbe(expected: T?) {
    assertEquals(expected, this)
}

fun Float.shouldbe(expected: Float, delta: Float) {
    val difference = differenceWith(expected)

    assertTrue("Expected $expected | Got $this [difference=$difference | allowable=$delta]") {
        difference <= delta
    }
}

fun Float.differenceWith(expected: Float): Float =
    if (this > expected) this - expected
    else expected - this
