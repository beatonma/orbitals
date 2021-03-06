package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Momentum
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test

class CollisionTest {

    @Test
    fun testMergeCollision() {
        val larger = InertialBody(
            mass = 60.kg,
            radius = 1.metres,
            position = Position(0, 0),
            velocity = Velocity(1, 0)
        )
        val smaller = InertialBody(
            mass = 50.kg,
            radius = 1.metres,
            position = Position(1, 0),
            velocity = Velocity(-1, 0)
        )

        val before = Tracker(larger.copy(), smaller.copy())
        applyCollision(larger, smaller, CollisionStyle.Merge)
        val after = Tracker(larger.copy(), smaller.copy())

        println("Before: $before")
        println("After: $after")

        before.totalMass shouldbe after.totalMass
        before.totalMomentum shouldbe after.totalMomentum
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
