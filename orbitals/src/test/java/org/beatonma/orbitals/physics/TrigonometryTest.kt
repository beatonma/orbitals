package org.beatonma.orbitals.physics

import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test


class TrigonometryTest {
    @Test
    fun testSquareOf() {
        squareOf(2f) shouldbe 4f
        squareOf(3f) shouldbe 9f
        squareOf(-1f) shouldbe 1f
    }

    @Test
    fun testAngle() {
        0f.degrees.asRadians shouldbe 0f
        0f.radians.asDegrees shouldbe 0f

        45f.degrees.asRadians.shouldbe(0.78f, 0.01f)
        1f.radians.asDegrees.shouldbe(57.29f, 0.01f)

        180f.degrees.asRadians.shouldbe(3.14f, 0.01f)
        270f.degrees.asRadians.shouldbe(4.71f, 0.01f)

        // Conversion to radians and back to degrees should be lossless
        45f.degrees.asRadians.radians.asDegrees shouldbe 45f
        225f.degrees.asRadians.radians.asDegrees shouldbe 225f

        // Conversion to degrees and back to radians should be lossless
        1f.radians.asDegrees.degrees.asRadians shouldbe 1f
    }
}
