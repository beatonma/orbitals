package org.beatonma.orbitals.test

import org.beatonma.orbitals.physics.Scalar
import org.beatonma.orbitals.physics.Vector2D
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue


infix fun <T> T?.shouldbe(expected: T?) {
    assertEquals(expected, this)
}

infix fun <T, L: List<T>> L.shouldbe(expected: L) {
    assertContentEquals(expected, this)
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


infix fun Scalar.shouldbe(expected: Scalar) {
    value.shouldbe(expected.value, delta = 0.01f)
}

infix fun <T : Scalar, V : Vector2D<T>> V.shouldbe(expected: V) {
    x.value.shouldbe(expected.x.value, delta = 0.01f)
    y.value.shouldbe(expected.y.value, delta = 0.01f)
}
