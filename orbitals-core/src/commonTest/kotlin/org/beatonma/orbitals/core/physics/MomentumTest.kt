package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test

class MomentumTest {
    @Test
    fun testMomentumMath() {
        val v = Velocity(1, 7)
        val mass = 15.kg

        mass * v shouldbe v * mass
        val momentum = mass * v

        momentum shouldbe Momentum(15, 105)

        momentum / v shouldbe mass
        momentum / mass shouldbe v
    }
}
