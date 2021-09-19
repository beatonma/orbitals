package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.test.differenceWith
import org.beatonma.orbitals.test.shouldbe
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BodyTest {
    private val body
        get() = InertialBody(
            mass = 7000.kg,
            radius = 10.metres,
            motion = ZeroMotion,
        )

    private val other
        get() = InertialBody(
            mass = 3000.kg,
            radius = 4.metres,
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
            distance.value.shouldbe(500.0f, delta = 0.01f)
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

            val force: Force = body.calculateForce(other, DefaultG)
            force.value.shouldbe(5.606e2f, delta = 1f)
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
            val force = body.calculateForce(other, DefaultG)
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
            distanceBefore.value.shouldbe(100f, delta = 0.0001f)

            body.applyGravity(other, 1000.ms, DefaultG)
            body.applyInertia(1000.ms)

            val distanceAfter = body.distanceTo(other)

            if (distance == null) {
                distance = distanceAfter
            } else {
                distanceAfter.value.shouldbe(93.326f, delta = 0.0001f)
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
}

@OptIn(ExperimentalTime::class)
private val Int.ms
    get() = Duration.milliseconds(this)
