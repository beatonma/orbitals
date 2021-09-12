package org.beatonma.orbitals.physics

import org.beatonma.orbitals.test.shouldbe
import org.junit.Test

class MathTest {
    @Test
    fun test_Int_factorial() {
        1.factorial shouldbe 1
        2.factorial shouldbe 2
        3.factorial shouldbe 6
        4.factorial shouldbe 24
        5.factorial shouldbe 120
    }
}
