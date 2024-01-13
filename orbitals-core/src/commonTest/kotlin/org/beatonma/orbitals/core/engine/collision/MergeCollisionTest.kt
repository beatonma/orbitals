package org.beatonma.orbitals.core.engine.collision

import org.beatonma.orbitals.core.engine.applyCollision
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Momentum
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.test.inertialBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private fun assertConservationOfMomentum(larger: InertialBody, smaller: InertialBody) {
    val originalMass = larger.mass + smaller.mass
    val originalMomentum = larger.momentum + smaller.momentum

    val originalLarger = larger.copy()
    val originalSmaller = smaller.copy()

    applyCollision(larger, smaller, CollisionStyle.Merge)

    assertEquals(originalMass, larger.mass + smaller.mass)
    assertEquals(originalMomentum, larger.momentum + smaller.momentum)

    // ...make sure that something actually happened
    assertFalse(originalLarger.physicsEquals(larger), message = "No change after collision.")
    assertFalse(originalSmaller.physicsEquals(smaller), message = "No change after collision.")
}

class MergeCollisionTest {
    @Test
    fun testSimpleCollision() {
        val larger = inertialBody(
            mass = 60.kg,
            radius = 1.metres,
            position = Position(0, 0),
            velocity = Velocity(1, 0)
        )
        val smaller = inertialBody(
            mass = 50.kg,
            radius = 1.metres,
            position = Position(0, 0),
            velocity = Velocity(-2, 0)
        )

        assertConservationOfMomentum(larger, smaller)
    }
}


private data class Tracker(val body: Body, val other: Body) {
    val totalMass: Mass = body.mass + other.mass
    val totalVelocity: Velocity = body.velocity + other.velocity
    val totalMomentum: Momentum = body.momentum + other.momentum

    override fun toString(): String =
        "total ${totalMass}, $totalVelocity, $totalMomentum\n${describe(body)}\nvs\n${describe(other)}\n\n"

    private fun describe(b: Body): String = "${b.mass}, ${b.velocity}, ${b.momentum}"
}
