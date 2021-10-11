@file:OptIn(ExperimentalTime::class)

package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


class OrbitalsEngineTest {
    @Test
    fun pruneBodies_isCorrect() {
        val space = Region(-100, -100, 100, 100)

        val bodies = listOf(
            inertialBody(-101, 10), // remove
            inertialBody(-99, 10), // keep
            fixedBody(-15, -15), // keep
            greatAttractor(0, -300, age = Duration.seconds(100)), // remove
            inertialBody(10, -99), // keep
            inertialBody(10, -101), // remove
            fixedBody(15, 15, age = Duration.seconds(100)), // keep (convert to InertialBody)
            greatAttractor(300, 0), // keep
        )

        val (keep, destroy) =
            pruneBodies(bodies, space, Duration.seconds(60), keepAgedRandomizer = { false })

        keep.sortedBy { it.position } shouldbe listOf(
            inertialBody(-99, 10),
            fixedBody(-15, -15),
            inertialBody(10, -99),
            inertialBody(15, 15),
            greatAttractor(300, 0),
        ).sortedBy { it.position }

        destroy.sortedBy { it.position } shouldbe listOf(
            inertialBody(-101, 10),
            greatAttractor(0, -300, Duration.seconds(100)),
            inertialBody(10, -101),
        )
    }
}


private fun inertialBody(
    x: Number,
    y: Number,
) = InertialBody(
    id = UniqueID("FixedID"),
    mass = 100.kg,
    motion = Motion(Position(x, y)),
)


private fun fixedBody(
    x: Number,
    y: Number,
    age: Duration = Duration.seconds(0),
) = FixedBody(
    id = UniqueID("FixedID"),
    mass = 100.kg,
    motion = Motion(Position(x, y)),
    age = age,
)

private fun greatAttractor(
    x: Number,
    y: Number,
    age: Duration = Duration.seconds(0),
) = GreatAttractor(
    id = UniqueID("FixedID"),
    mass = 100.kg,
    motion = Motion(Position(x, y)),
    age = age,
)
