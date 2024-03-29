package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.test.DefaultTestDensity
import org.beatonma.orbitals.core.test.DefaultTestG
import org.beatonma.orbitals.core.test.fixedBody
import org.beatonma.orbitals.core.test.inertialBody
import org.beatonma.orbitals.test.differenceWith
import org.beatonma.orbitals.test.shouldbe
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

private const val TestG = DefaultTestG

class BodyTest {
    private val body
        get() = inertialBody(
            mass = 7000.kg,
            density = DefaultTestDensity,
            motion = ZeroMotion,
        )

    private val other
        get() = inertialBody(
            mass = 3000.kg,
            density = DefaultTestDensity,
            motion = Motion(
                Position(0, 500),
            )
        )

    @Test
    fun testDistanceTo_isCorrectInAllDirections() {
        doAround(radius = 500.metres) { _, x, y ->
            val body = body
            val other = other.copy(
                motion = Motion(
                    Position(x, y)
                )
            )

            val distance = body.distanceTo(other)
            distance.value shouldbe 500f
        }
    }

    @Test
    fun testCalculateForce_isCorrectInAllDirections() {
        doAround(radius = 500.metres) { _, x, y ->
            val body = body
            val other = other.copy(
                motion = Motion(
                    Position(x, y)
                )
            )

            val force: Force = body.calculateForce(other, TestG)
            force.value shouldbe 560.616f
        }
    }

    @Test
    fun testCalculateAcceleration_isCorrectInAllDirections() {
        doAround(radius = 500.metres) { _, x, y ->
            val body = body
            val other = other.copy(
                motion = Motion(
                    Position(x, y),
                )
            )

            val angle = body.position.angleTo(other.position)
//
            val force = body.calculateForce(other, TestG)
            val acceleration = body.calculateAcceleration(force, angle)

            val absAcceleration = force / body.mass
            acceleration shouldbe Acceleration(
                x = absAcceleration * cos(angle),
                y = absAcceleration * sin(angle),
            )
        }
    }

    @Test
    fun testApplyGravity() {
        var distance: Distance? = null

        // Ensure that gravity is applied as expected in all directions.
        doAround(radius = 100.metres) { degrees, x, y ->
            val body = body.copy(mass = 10_000.kg)

            val other = other.copy(
                mass = 10_000.kg,
                motion = Motion(
                    Position(x, y)
                )
            )

            val distanceBefore = body.distanceTo(other)
            distanceBefore.value shouldbe 100f

            body.applyGravity(other, 1000.ms, TestG)
            body.applyInertia(1000.ms)

            val distanceAfter = body.distanceTo(other)

            if (distance == null) {
                distance = distanceAfter
            } else {
                distanceAfter.value shouldbe 93.326f
            }

            val errorMessage = when {
                distanceBefore == distanceAfter -> "No change"
                distanceAfter > distanceBefore -> "Wrong way! $distanceBefore -> $distanceAfter (${
                    distanceBefore.value.differenceWith(
                        distanceAfter.value
                    )
                })"

                else -> "" // No error
            }

            assertTrue(
                "Bodies should move towards each other under gravity but " +
                        "$errorMessage ($degrees:[$x, $y]) ${other.velocity}"
            ) {
                distanceBefore > distanceAfter
            }
        }
    }

    @Test
    fun testApplyInertia() {
        val body = body.copy(
            motion = Motion(
                Position(10, 30),
                Velocity(5, 10)
            )
        )
        body.position shouldbe Position(10, 30)

        body.applyInertia(1000.ms)
        body.position shouldbe Position(15, 40)

        body.applyInertia(500.ms)
        body.position shouldbe Position(17.5, 45.0)
    }

    @Test
    fun testCenterOfMass_withEqualMass() {
        val mass = 10.kg
        centerOfMass(
            inertialBody(mass, position = Position(-10, 0)),
            inertialBody(mass, position = Position(10, 0))
        ) shouldbe Position(0, 0)

        centerOfMass(
            inertialBody(mass, position = Position(-20, 0)),
            inertialBody(mass, position = Position(10, 0))
        ) shouldbe Position(-5, 0)

        centerOfMass(
            inertialBody(mass, position = Position(-10, 0)),
            inertialBody(mass, position = Position(20, 0))
        ) shouldbe Position(5, 0)

        centerOfMass(
            inertialBody(mass, position = Position(5, 10)),
            inertialBody(mass, position = Position(15, 20))
        ) shouldbe Position(10, 15)

        centerOfMass(
            inertialBody(mass, position = Position(-5, 10)),
            inertialBody(mass, position = Position(15, -20))
        ) shouldbe Position(5, -5)

        centerOfMass(
            inertialBody(mass, position = Position(15, -20)),
            inertialBody(mass, position = Position(-5, 10))
        ) shouldbe Position(5, -5)
    }

    @Test
    fun testCenterOfMass_withDifferentMass() {
        centerOfMass(
            inertialBody(10.kg, position = Position(-10, 0)),
            inertialBody(20.kg, position = Position(10, 0))
        ) shouldbe Position(3.33, 0)

        centerOfMass(
            inertialBody(20.kg, position = Position(-10, 0)),
            inertialBody(10.kg, position = Position(10, 0))
        ) shouldbe Position(-3.33, 0)

        centerOfMass(
            inertialBody(5.kg, position = Position(-10, 35)),
            inertialBody(15.kg, position = Position(10, -5))
        ) shouldbe Position(5, 5)
    }

    @Test
    fun testInContactWith() {
        inertialBody().inContactWith(inertialBody()) shouldbe true
        fixedBody().inContactWith(inertialBody()) shouldbe true

        inertialBody(radius = 1.metres).inContactWith(
            inertialBody(
                position = Position(2, 0),
                radius = 1.metres
            )
        ) shouldbe true

        inertialBody(radius = .95.metres).inContactWith(
            inertialBody(
                position = Position(2, 0),
                radius = 1.metres
            )
        ) shouldbe false
    }
}

private val Int.ms
    get() = this.milliseconds
