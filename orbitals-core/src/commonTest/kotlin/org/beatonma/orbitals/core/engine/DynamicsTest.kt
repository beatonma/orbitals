package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.test.fixedBody
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test

class DynamicsTest {
    @Test
    fun testBodyOverlap() {
        val originBody = fixedBody(radius = 20.metres)
        val movingBody = fixedBody(radius = 10.metres)

        overlapOf(originBody, movingBody) shouldbe 1f

        overlapOf(
            originBody,
            movingBody.apply { position = Position(30.metres, 0.metres) }
        ) shouldbe 0f

        overlapOf(
            originBody,
            movingBody.apply { position = Position(20.metres, 0.metres) }
        ) shouldbe .5f

        overlapOf(
            originBody,
            movingBody.apply { position = Position(15.metres, 0.metres) }
        ) shouldbe .75f
    }
}
