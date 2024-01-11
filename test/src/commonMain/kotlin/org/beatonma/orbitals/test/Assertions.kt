package org.beatonma.orbitals.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

infix fun <T> T?.shouldbe(expected: T?) {
    assertEquals(expected, this)
}

infix fun <T> List<T>.shouldbe(expected: List<T>) {
    assertContentEquals(expected, this)
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
