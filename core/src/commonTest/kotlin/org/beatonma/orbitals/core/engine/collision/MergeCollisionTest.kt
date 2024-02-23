package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.applyCollision
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.test.DefaultColliderAge
import org.beatonma.orbitals.core.test.inertialBody
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private fun assertConservationOfMomentum(
    larger: InertialBody,
    smaller: InertialBody
): CollisionResults? {
    val originalMass = larger.mass + smaller.mass
    val originalMomentum = larger.momentum + smaller.momentum

    val originalLarger = larger.copy()
    val originalSmaller = smaller.copy()

    val result = applyCollision(larger, smaller, CollisionStyle.Merge)

    assertEquals(originalMass, larger.mass + smaller.mass)
    assertEquals(originalMomentum, larger.momentum + smaller.momentum)

    // ...make sure that something actually happened
    assertFalse(
        originalLarger.physicsEquals(larger),
        message = "No change after collision. $larger"
    )
    assertFalse(
        originalSmaller.physicsEquals(smaller),
        message = "No change after collision. $smaller"
    )

    return result
}

class MergeCollisionTest {
    @Test
    fun testSimpleCollision() {
        val larger = inertialBody(
            mass = 60.kg,
            radius = 1.metres,
            position = Position(0, 0),
            velocity = Velocity(1, 0),
            age = DefaultColliderAge,
        )
        val smaller = inertialBody(
            mass = 50.kg,
            radius = 1.metres,
            position = Position(0, .5),
            velocity = Velocity(-2, 0),
            age = DefaultColliderAge,
        )

        assertConservationOfMomentum(larger, smaller)
    }

    @Test
    fun testOverlapping() {
        val a = inertialBody(mass = 51.kg, radius = 10.metres, age = DefaultColliderAge)
        val b = inertialBody(mass = 50.kg, radius = 10.metres, age = DefaultColliderAge)

        val result = assertConservationOfMomentum(
            a,
            b,
        )

        result!!.removed.size shouldbe 1
    }
}
