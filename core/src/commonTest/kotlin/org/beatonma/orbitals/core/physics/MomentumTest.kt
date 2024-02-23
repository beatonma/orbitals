package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumTest {
    @Test
    fun testMomentumConversions() {
        val v = Velocity(1, 7)
        val mass = 15.kg

        mass * v shouldbe v * mass
        val momentum = mass * v

        momentum shouldbe Momentum(15, 105)

        momentum / v shouldbe mass
        momentum / mass shouldbe v
    }

    @Test
    fun testMomentumMultiply() {
        val momentum: Momentum = Velocity(1, 0) * 2.kg

        assertEquals(momentum.magnitude * 2f, (momentum * 2f).magnitude)
        assertEquals(momentum.magnitude * .4f, (momentum * .4f).magnitude)
    }
}
