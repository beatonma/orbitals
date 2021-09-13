package org.beatonma.orbitals.physics

import org.beatonma.orbitals.test.shouldbe
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
}
