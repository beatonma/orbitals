package org.beatonma.orbitalslivewallpaper.orbitals.physics

import org.beatonma.orbitalslivewallpaper.test.differenceWith
import org.beatonma.orbitalslivewallpaper.test.shouldbe
import kotlin.test.Test
import kotlin.test.assertTrue

//@OptIn(kotlin.time.ExperimentalTime::class)
//class BodyTest {
//    private val body
//        get() = InertialBody(
//            mass = 7000.kg,
//            radius = 10.metres,
//        )
//
//    private val other
//        get() = InertialBody(
//            mass = 3000.kg,
//            radius = 4.metres,
//            motion = Motion(
//                Position(0, 500),
//            )
//        )
//
//    @Test
//    fun testDistanceTo_isCorrectInAllDirections() {
//        doAround(radius = 500f) { _, x, y ->
//            val body = body
//            val other = other.copy(
//                motion = Motion(
//                    Position(x, y)
//                )
//            )
//
//            val distance = body.distanceTo(other)
//            distance.metres.shouldbe(500.0f, delta = 0.01f)
//        }
//    }
//
//    @Test
//    fun testCalculateForce_isCorrectInAllDirections() {
//        doAround(radius = 500f) { _, x, y ->
//            val body = body
//            val other = other.copy(
//                motion = Motion(
//                    Position(x, y)
//                )
//            )
//
//            val force: Force = body.calculateForce(other)
//            force.newtons.shouldbe(5.606e2f, delta = 1f)
//        }
//    }
//
//    @Test
//    fun testCalculateAcceleration_isCorrectInAllDirections() {
//        doAround(radius = 500f) { _, x, y ->
//            val body = body
//            val other = other.copy(
//                motion = Motion(
//                    Position(x, y),
//                )
//            )
//
//            val force = body.calculateForce(other)
//            val acceleration = body.calculateAcceleration(force)
//            acceleration.value.shouldbe(8e-2f, delta = 0.1f)
//        }
//    }
//
//    @Test
//    fun testApplyGravity() {
//        // Ensure that gravity is applied as expected in all directions.
//        doAround(radius = 100f) { degrees, x, y ->
//            val body = body.copy(mass = 10_000.kg)
//            val other = other.copy(
//                mass = 10_000.kg,
//                motion = Motion(
//                    Position(x, y)
//                )
//            )
//
//            body.calculateForce(other).newtons.shouldbe(66740f, delta = 0.01f)
//            val distanceBefore = body.distanceTo(other)
//
//            body.applyGravity(other)
//            body.applyInertia(10000.ms)
//
//            val distanceAfter = body.distanceTo(other)
//
//            val errorMessage = when {
//                distanceBefore == distanceAfter -> "No change"
//                distanceAfter > distanceBefore -> "Wrong way! $distanceBefore -> $distanceAfter (${
//                    distanceBefore.metres.differenceWith(
//                        distanceAfter.metres
//                    )
//                })"
//                else -> "" // No error
//            }
//
//            assertTrue(
//                "Bodies should move towards each other under gravity but " +
//                        "$errorMessage ($degrees:[$x, $y]) ${other.velocity}"
//            ) {
//                distanceBefore > distanceAfter
//            }
//        }
//    }
//
//    @Test
//    fun testApplyInertia() {
//        val body = body.copy(
//            motion = Motion(
//                Position(10, 30),
//                Velocity(5, 10)
//            )
//        )
//        body.position shouldbe Position(10, 30)
//
//        body.applyInertia(1000.ms)
//        body.position shouldbe Position(15, 40)
//
//        body.applyInertia(500.ms)
//        body.position shouldbe Position(17.5, 45.0)
//    }
//}
