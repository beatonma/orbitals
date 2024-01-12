package org.beatonma.orbitals.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

infix fun <T> T?.shouldbe(expected: T?) {
    assertEquals(expected, this)
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
