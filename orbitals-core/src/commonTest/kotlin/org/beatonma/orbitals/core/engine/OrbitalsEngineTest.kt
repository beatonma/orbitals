package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.test.DefaultTestDensity
import org.beatonma.orbitals.core.test.DefaultTestMass
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class OrbitalsEngineTest {
    @Test
    fun pruneBodies_isCorrect() {
        val space = Region(-100, -100, 100, 100)

        val bodies = listOf(
            inertialBody(-101, 10), // remove
            inertialBody(-99, 10), // keep
            fixedBody(-15, -15), // keep
            greatAttractor(0, -300, age = 100.seconds), // remove
            inertialBody(10, -99), // keep
            inertialBody(10, -101), // remove
            fixedBody(15, 15, age = 100.seconds), // keep (convert to InertialBody)
            greatAttractor(300, 0), // keep
        )

        val (keep, destroy) =
            pruneBodies(bodies, space, 60.seconds, keepAgedRandomizer = { false })

        keep.sortedBy { it.position } shouldbe listOf(
            inertialBody(-99, 10),
            fixedBody(-15, -15),
            inertialBody(10, -99),
            inertialBody(15, 15),
            greatAttractor(300, 0),
        ).sortedBy { it.position }

        destroy.sortedBy { it.position } shouldbe listOf(
            inertialBody(-101, 10),
            greatAttractor(0, -300, 100.seconds),
            inertialBody(10, -101),
        )
    }
}


private fun inertialBody(
    x: Number,
    y: Number,
) = InertialBody(
    id = UniqueID("FixedID"),
    mass = DefaultTestMass,
    density = DefaultTestDensity,
    motion = Motion(Position(x, y)),
)


private fun fixedBody(
    x: Number,
    y: Number,
    age: Duration = 0.seconds,
) = FixedBody(
    id = UniqueID("FixedID"),
    mass = DefaultTestMass,
    density = DefaultTestDensity,
    motion = Motion(Position(x, y)),
    age = age,
)

private fun greatAttractor(
    x: Number,
    y: Number,
    age: Duration = 0.seconds,
) = GreatAttractor(
    id = UniqueID("FixedID"),
    mass = DefaultTestMass,
    density = DefaultTestDensity,
    motion = Motion(Position(x, y)),
    age = age,
)
