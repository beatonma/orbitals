package org.beatonma.orbitalslivewallpaper.orbitals.physics

import org.beatonma.orbitals.physics.Velocity
import org.beatonma.orbitalslivewallpaper.test.shouldbe as FloatShouldBe
import kotlin.test.Test

class VelocityTest {
    private infix fun Float.shouldbe(expected: Float) =
        this.FloatShouldBe(expected, delta = 0.01f)

    @Test
    fun testVelocity() {
        Velocity(0, 0).run {
            vector.magnitude shouldbe 0f
        }

        Velocity(1, 0).run {
            vector.magnitude shouldbe 1f
            angle.asDegrees shouldbe 0f
        }

        Velocity(1, 1).run {
            vector.magnitude shouldbe 1.41f
            angle.asDegrees shouldbe 45f
        }

        Velocity(0, 1).run {
            vector.magnitude shouldbe 1f
            angle.asDegrees shouldbe 90f
        }

        Velocity(-1, 1).run {
            vector.magnitude shouldbe 1.41f
            angle.asDegrees shouldbe 135f
        }

        Velocity(-1, 0).run {
            vector.magnitude shouldbe 1f
            angle.asDegrees shouldbe 180f
        }

        Velocity(-1, -1).run {
            vector.magnitude shouldbe 1.41f
            angle.asDegrees shouldbe -135f
        }

        Velocity(0, -1).run {
            vector.magnitude shouldbe 1.0f
            angle.asDegrees shouldbe -90.0f
        }

        Velocity(1, -1).run {
            vector.magnitude shouldbe 1.41f
            angle.asDegrees shouldbe -45f
        }
    }
}
