package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.test.DefaultTestDensity
import kotlin.test.Test
import org.beatonma.orbitals.core.test.shouldbe

class VelocityTest {
    private infix fun Float.shouldbe(expected: Float) =
        this.shouldbe(expected, delta = 0.01f)

    @Test
    fun testVelocity() {
        Velocity(0, 0).run {
            magnitude.value shouldbe 0f
        }

        Velocity(1, 0).run {
            magnitude.value shouldbe 1f
            angle.asDegrees shouldbe 0f
        }

        Velocity(1, 1).run {
            magnitude.value shouldbe 1.41f
            angle.asDegrees shouldbe 45f
        }

        Velocity(0, 1).run {
            magnitude.value shouldbe 1f
            angle.asDegrees shouldbe 90f
        }

        Velocity(-1, 1).run {
            magnitude.value shouldbe 1.41f
            angle.asDegrees shouldbe 135f
        }

        Velocity(-1, 0).run {
            magnitude.value shouldbe 1f
            angle.asDegrees shouldbe 180f
        }

        Velocity(-1, -1).run {
            magnitude.value shouldbe 1.41f
            angle.asDegrees shouldbe 225f
        }

        Velocity(0, -1).run {
            magnitude.value shouldbe 1.0f
            angle.asDegrees shouldbe 270.0f
        }

        Velocity(1, -1).run {
            magnitude.value shouldbe 1.41f
            angle.asDegrees shouldbe 315f
        }
    }

    @Test
    fun testVelocityMath() {
        (Velocity(1, 2) + Velocity(7, 11)) shouldbe Velocity(8, 13)
        var v = Velocity(1, 2)
        v += Velocity(7, 11)
        v.shouldbe(Velocity(8, 13))

        val body = InertialBody(
            UniqueID(""),
            mass = 500.kg,
            density = DefaultTestDensity,
            motion = ZeroMotion
        )
        body.velocity += Velocity(7, 11)
        body.velocity shouldbe Velocity(7, 11)
        body.velocity += Velocity(5, 13)
        body.velocity shouldbe Velocity(12, 24)
    }
}
