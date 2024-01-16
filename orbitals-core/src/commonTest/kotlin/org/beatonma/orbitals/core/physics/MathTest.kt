package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.divideUnevenly
import org.beatonma.orbitals.core.factorial
import org.beatonma.orbitals.core.mapTo
import org.beatonma.orbitals.core.test.shouldbe
import org.beatonma.orbitals.core.test.shouldbePositive
import org.beatonma.orbitals.test.shouldbe
import kotlin.random.Random
import kotlin.test.Test

class MathTest {
    @Test
    fun test_Int_factorial() {
        1.factorial shouldbe 1
        2.factorial shouldbe 2
        3.factorial shouldbe 6
        4.factorial shouldbe 24
        5.factorial shouldbe 120
    }

    @Test
    fun testSquareOf() {
        org.beatonma.orbitals.core.squareOf(2f) shouldbe 4f
        org.beatonma.orbitals.core.squareOf(3f) shouldbe 9f
        org.beatonma.orbitals.core.squareOf(-1f) shouldbe 1f
    }

    @Test
    fun testDivUneven() {
        fun assertResults(total: Float, chunks: Int) {
            val divided =
                total.divideUnevenly(chunks, variance = Random.nextFloat().mapTo(.1f, .9f))
            println(divided)

            divided.size shouldbe chunks
            divided.sum().shouldbe(total, delta = 0.001f)
            divided.forEach(Float::shouldbePositive)
            divided.groupBy { it }.size shouldbe chunks // Values should be different
        }

        assertResults(2f, 3)
        assertResults(2f, 2)
        assertResults(2f, 1)

        repeat(50) {
            val n = Random.nextInt(2, 10)
            val total = Random.nextFloat() * 50f
            assertResults(total, n)
        }
    }
}
