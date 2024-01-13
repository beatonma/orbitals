package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.test.fixedBody
import org.beatonma.orbitals.core.test.greatAttractor
import org.beatonma.orbitals.core.test.inertialBody
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

        keep.sortedBy(Body::position).shouldbe(
            listOf(
                inertialBody(-99, 10),
                fixedBody(-15, -15),
                inertialBody(10, -99),
                inertialBody(15, 15, age = 100.seconds),
                greatAttractor(300, 0),
            )
        ) { actual, expected ->
            actual.physicsEquals(expected)
        }

        destroy.sortedBy(Body::position).shouldbe(
            listOf(
                inertialBody(-101, 10),
                greatAttractor(0, -300, 100.seconds),
                inertialBody(10, -101),
            )
        ) { actual, expected ->
            actual.physicsEquals(expected)
        }
    }
}


private fun inertialBody(
    x: Int,
    y: Int,
    age: Duration = 0.seconds,
) = inertialBody(
    motion = Motion(Position(x, y)),
    age = age,
)


private fun fixedBody(
    x: Int,
    y: Int,
    age: Duration = 0.seconds,
) = fixedBody(
    motion = Motion(Position(x, y)),
    age = age,
)

private fun greatAttractor(
    x: Int,
    y: Int,
    age: Duration = 0.seconds,
) = greatAttractor(
    motion = Motion(Position(x, y)),
    age = age,
)
