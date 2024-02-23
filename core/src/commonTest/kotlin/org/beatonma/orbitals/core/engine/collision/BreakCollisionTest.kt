package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.applyCollision
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.test.DefaultColliderAge
import org.beatonma.orbitals.core.test.inertialBody
import kotlin.test.Test
import kotlin.test.assertNotEquals

class BreakCollisionTest {
    @Test
    fun testBreak() {
        val result = applyCollision(
            inertialBody(radius = 1.metres, velocity = Velocity(10, 5), age = DefaultColliderAge),
            inertialBody(position = Position(1, 0), radius = 1.metres, age = DefaultColliderAge),
            CollisionStyle.Break
        )!!

        assertNotEquals(0, result.added.size)
    }
}
