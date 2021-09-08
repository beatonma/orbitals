package org.beatonma.orbitalslivewallpaper.orbitals.physics

import org.beatonma.orbitals.physics.*
import org.beatonma.orbitalslivewallpaper.test.shouldbe
import kotlin.test.Test


class PositionTest {
    private infix fun Float.shouldbe(expected: Float) {
        this.shouldbe(expected, delta = 0.001f)
    }

    private infix fun Distance.shouldbe(expected: Float) {
        this.metres.shouldbe(expected, delta = 0.001f)
    }

    @Test
    fun gradientTo() {
        ZeroPosition.gradientTo(Position(1, 1)) shouldbe 1.0f
        ZeroPosition.gradientTo(Position(1, 0)) shouldbe 0.0f
        ZeroPosition.gradientTo(Position(0, 1)) shouldbe 1.0e5f
        ZeroPosition.gradientTo(Position(-1, 1)) shouldbe -1.0f
        ZeroPosition.gradientTo(Position(-1, 0)) shouldbe 0.0f
        ZeroPosition.gradientTo(Position(-1, -1)) shouldbe 1.0f
        ZeroPosition.gradientTo(Position(0, -1)) shouldbe -1.0e5f
        ZeroPosition.gradientTo(Position(1, -1)) shouldbe -1.0f

        Position(2, 4).gradientTo(Position(1, 1)) shouldbe 3.0f
        ZeroPosition.gradientTo(ZeroPosition) shouldbe 0.0f
    }

    @Test
    fun distanceTo() {
        ZeroPosition.distanceTo(Position(2.0, 0.0)) shouldbe 2.0f
        ZeroPosition.distanceTo(Position(0.0, 2.0)) shouldbe 2.0f

        ZeroPosition.distanceTo(Position(1.0, 1.0)) shouldbe 1.414f
        ZeroPosition.distanceTo(Position(-1.0, 1.0)) shouldbe 1.414f
        ZeroPosition.distanceTo(Position(1.0, -1.0)) shouldbe 1.414f
        ZeroPosition.distanceTo(Position(-1.0, -1.0)) shouldbe 1.414f
    }
}
