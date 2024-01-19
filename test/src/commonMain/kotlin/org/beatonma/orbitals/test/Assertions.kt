package org.beatonma.orbitals.test

import org.beatonma.orbitals.core.physics.Scalar
import org.beatonma.orbitals.core.physics.Vector2D
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val FloatAllowableDifference = 0.01f

infix fun <T : Any> T.shouldbe(expected: T) {
    assertEquals(expected, this)
}

infix fun Float.shouldbe(expected: Float) {
    this.shouldbe(expected, FloatAllowableDifference)
}

fun Float.shouldbe(expected: Float, delta: Float) {
    val difference = differenceWith(expected)

    assertTrue("Expected $expected | Got $this [difference=$difference | allowable=$delta]") {
        difference <= delta
    }
}

fun Float.differenceWith(expected: Float): Float = when {
    this > expected -> this - expected
    else -> expected - this
}

fun Float.shouldbeGreaterThan(other: Float) {
    assertTrue("$this shouldbe > $other") { this > other }
}

fun Float.shouldbePositive() = shouldbeGreaterThan(0f)

infix fun <T : Scalar> T.shouldbe(expected: T) {
    this.value.shouldbe(expected.value, FloatAllowableDifference)
}

infix fun <T : Scalar> Vector2D<T>.shouldbe(expected: Vector2D<T>) {
    this.x shouldbe expected.x
    this.y shouldbe expected.y
}

infix fun <T> List<T>.shouldbe(expected: List<T>) {
    assertContentEquals(expected, this)
}

/**
 * Compare each item by [transform].
 */
fun <T> List<T>.shouldbe(expected: List<T>, transform: (actual: T, expected: T) -> Boolean) {
    assertEquals(
        this.size,
        expected.size,
        message = "Lists have different sizes: ${this.size} vs ${expected.size}"
    )

    for (i in indices) {
        val actualValue = this[i]
        val expectedValue = expected[i]
        assertTrue(
            transform(expectedValue, actualValue),
            message = "Items at #$i differ: Expected ${expectedValue}; got $actualValue"
        )
    }
}

infix fun <T> Array<T>.shouldbe(expected: Array<T>) {
    assertContentEquals(expected, this)
}

infix fun FloatArray.shouldbe(expected: FloatArray) {
    assertContentEquals(expected, this)
}

inline fun <reified E : Exception> assertThrows(message: String? = null, block: () -> Unit) {
    var raised = false
    try {
        block()
    } catch (e: Exception) {
        if (e is E) {
            raised = true
        }
    }

    if (!raised) {
        val messages = listOfNotNull(
            "Expected error ${E::class} was not raised!",
            message
        ).joinToString(" ")
        throw AssertionError(messages)
    }
}
