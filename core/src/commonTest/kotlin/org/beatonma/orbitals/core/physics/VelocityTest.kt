package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.test.inertialBody
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test

class VelocityTest {
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
            magnitude.value shouldbe 1.414f
            angle.asDegrees shouldbe 45f
        }

        Velocity(0, 1).run {
            magnitude.value shouldbe 1f
            angle.asDegrees shouldbe 90f
        }

        Velocity(-1, 1).run {
            magnitude.value shouldbe 1.414f
            angle.asDegrees shouldbe 135f
        }

        Velocity(-1, 0).run {
            magnitude.value shouldbe 1f
            angle.asDegrees shouldbe 180f
        }

        Velocity(-1, -1).run {
            magnitude.value shouldbe 1.414f
            angle.asDegrees shouldbe 225f
        }

        Velocity(0, -1).run {
            magnitude.value shouldbe 1.0f
            angle.asDegrees shouldbe 270.0f
        }

        Velocity(1, -1).run {
            magnitude.value shouldbe 1.414f
            angle.asDegrees shouldbe 315f
        }
    }

    @Test
    fun testVelocityMath() {
        (Velocity(1, 2) + Velocity(7, 11)) shouldbe Velocity(8, 13)
        var v = Velocity(1, 2)
        v += Velocity(7, 11)
        v.shouldbe(Velocity(8, 13))

        val body = inertialBody(mass = 500.kg, velocity = ZeroVelocity)
        body.velocity += Velocity(7, 11)
        body.velocity shouldbe Velocity(7, 11)
        body.velocity += Velocity(5, 13)
        body.velocity shouldbe Velocity(12, 24)
    }
}
