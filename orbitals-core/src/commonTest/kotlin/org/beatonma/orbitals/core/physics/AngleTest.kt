package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.test.shouldbe
import kotlin.random.Random
import kotlin.test.Test


class AngleTest {
    private infix fun Float.shouldbe(expected: Float) {
        this.shouldbe(expected, 0.01f)
    }

    @Test
    fun testAngle() {
        0f.degrees.asRadians shouldbe 0f
        0f.radians.asDegrees shouldbe 0f

        45f.degrees.asRadians shouldbe 0.78f
        1f.radians.asDegrees shouldbe 57.29f

        180f.degrees.asRadians shouldbe 3.14f
        270f.degrees.asRadians shouldbe 4.71f

        355.degrees.asDegrees shouldbe 355f

        // Check values outside 0..360 degree are interpreted inside those limits.
        365.degrees.asDegrees shouldbe 5f
        730.degrees.asDegrees shouldbe 10f
        (-5).degrees.asDegrees shouldbe 355f
        (-370).degrees.asDegrees shouldbe 350f

        // rawDegrees should allow angles outside the normal limits
        365.rawDegrees.asDegrees shouldbe 365f
        730.rawDegrees.asDegrees shouldbe 730f
        (-5).rawDegrees.asDegrees shouldbe -5f
        (-370).rawDegrees.asDegrees shouldbe -370f


        // Conversion to radians and back to degrees should be lossless
        45f.degrees.asRadians.radians.asDegrees shouldbe 45f
        225f.degrees.asRadians.radians.asDegrees shouldbe 225f

        // Conversion to degrees and back to radians should be lossless
        1f.radians.asDegrees.degrees.asRadians shouldbe 1f
    }

    @Test
    fun testDirection() {
        0.degrees.toDirection() shouldbe Direction(1, 0)
        90.degrees.toDirection() shouldbe Direction(0, 1)
        180.degrees.toDirection() shouldbe Direction(-1, 0)
        270.degrees.toDirection() shouldbe Direction(0, -1)

        Direction(1, 0).toAngle() shouldbe 0.degrees
        Direction(0, 1).toAngle() shouldbe 90.degrees
        Direction(-1, 0).toAngle() shouldbe 180.degrees
        Direction(0, -1).toAngle() shouldbe 270.degrees
    }

    @Test
    fun testAngleMath() {
        0.degrees * 10.metres shouldbe Position(10, 0)
        90.degrees * 10.metres shouldbe Position(0, 10)
        180.degrees * 10.metres shouldbe Position(-10, 0)
        270.degrees * 10.metres shouldbe Position(0, -10)

        Direction(1, 0) * 10.metres shouldbe Position(10, 0)
        Direction(0, 1) * 10.metres shouldbe Position(0, 10)
        Direction(-1, 0) * 10.metres shouldbe Position(-10, 0)
        Direction(0, -1) * 10.metres shouldbe Position(0, -10)

        Velocity(5, 5).direction shouldbe Velocity(1, 1).direction
        Velocity(-5, 5).direction shouldbe Velocity(-1, 1).direction
        Velocity(5, -5).direction shouldbe Velocity(1, -1).direction
        Velocity(-5, -5).direction shouldbe Velocity(-1, -1).direction

        Velocity(5, 5).direction.magnitude shouldbe 1.metres
        Velocity(-5, 5).direction.magnitude shouldbe 1.metres
        Velocity(5, -5).direction.magnitude shouldbe 1.metres
        Velocity(-5, -5).direction.magnitude shouldbe 1.metres

        repeat(100) {
            Velocity(
                Random.nextInt(-100, 100),
                Random.nextInt(-100, 100)
            ).direction.magnitude shouldbe 1.metres
        }
    }
}
