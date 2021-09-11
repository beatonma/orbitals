package org.beatonma.orbitals.options

import org.beatonma.orbitals.Space
import org.beatonma.orbitals.chance
import org.beatonma.orbitals.mapTo
import org.beatonma.orbitals.percent
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.Mass
import org.beatonma.orbitals.physics.Motion
import org.beatonma.orbitals.physics.Velocity
import org.beatonma.orbitals.physics.getOrbitalMotion
import org.beatonma.orbitals.physics.kg
import org.beatonma.orbitals.physics.metres
import org.beatonma.orbitals.physics.uniqueID
import org.beatonma.orbitals.relativeVisiblePosition
import kotlin.random.Random

private const val MaxFixedBodies = 4

enum class SystemGenerator {

    /**
     * Generate a simple StarSystem in the center of the given Space with a few satellites of fixed
     * definition. Useful for debugging.
     */
    IdealStarSystem,

    /**
     * Generate a system of satellites orbiting a central star.
     */
    StarSystem,

    /**
     * Generate [InertialBody]s with random position, mass, size, velocity.
     */
    Randomized,

    /**
     * Generate a series of [FixedBody]s for bodies to navigate through.
     */
    Gauntlet,
    ;

    fun generate(space: Space, bodies: List<Body>, max: Int): List<Body> = when (this) {
        IdealStarSystem -> generateIdealStarSystem(space, bodies, max)
        StarSystem -> generateStarSystem(space, bodies, max)
        Randomized -> generateRandom(space, bodies, max)
        Gauntlet -> generateGauntlet(space, bodies, max)
    }

    private fun generateGauntlet(space: Space, bodies: List<Body>, max: Int): List<Body> {
        val fixedBodies = bodies.filterIsInstance<FixedBody>()

        val randomBodies = generateRandom(space, bodies, max)
        return if (fixedBodies.size > MaxFixedBodies) {
            randomBodies + createBodies(MaxFixedBodies - fixedBodies.size) { index, n ->
                val mass = largeMass()
                FixedBody(
                    id = uniqueID("gauntlet"),
                    mass = mass,
                    radius = sizeOf(mass),
                    motion = Motion(
                        space.relativeVisiblePosition(
                            index * (1f / n),
                            Random.nextFloat()
                        )
                    )
                )
            }
        }
        else {
            randomBodies
        }
    }

    private fun generateIdealStarSystem(space: Space, bodies: List<Body>, max: Int): List<Body> {
        if (bodies.find { it is FixedBody } != null) {
            return listOf()
        }

        val mass = 500.kg
        val sun = FixedBody(
            id = uniqueID("center"),
            mass = mass,
            radius = sizeOf(mass),
            motion = Motion(
                space.relativeVisiblePosition(0.5f, 0.5f)
            ),
        )

        return listOf(
            sun,
            satelliteOf(
                sun,
                space.radius.metres * .5f,
                1.kg,
                20.metres,
            ),
            satelliteOf(
                sun,
                space.radius.metres * .3f,
                1.kg,
                20.metres,
            )
        )
    }

    private fun generateStarSystem(space: Space, bodies: List<Body>, max: Int): List<Body> {
        val fixedBodies = bodies.filterIsInstance<FixedBody>()
        val useExistingStar = fixedBodies.size > 3

        val sun = if (useExistingStar) {
            fixedBodies.random()
        } else {
            val mass = largeMass()
            FixedBody(
                id = uniqueID("center"),
                mass = mass,
                radius = sizeOf(mass),
                motion = Motion(
                    space.relativeVisiblePosition(Random.nextFloat(), Random.nextFloat())
                ),
            )
        }

        val minDistance = (space.radius * .1f).metres
        val maxDistance = (space.radius * .9f).metres

        val satellites = createBodies(4) { _, _ ->
            satelliteOf(sun, anyDistance(minDistance, maxDistance))
        }

        return if (useExistingStar) {
            satellites
        } else {
            listOf(sun) + satellites
        }
    }

    private fun generateRandom(space: Space, bodies: List<Body>, max: Int): List<Body> {
        return createBodies { index, _ ->
            val mass = anyMass()
            InertialBody(
                id = uniqueID("random[$index]"),
                mass = mass,
                radius = sizeOf(mass),
                motion = Motion(
                    space.relativeVisiblePosition(Random.nextFloat(), Random.nextFloat()),
                    Velocity(Random.nextInt(0, 5), Random.nextInt(0, 5))
                )
            )
        }
    }
}

private fun createBodies(range: Int = 10, transform: (index: Int, n: Int) -> Body): List<Body> {
    val size = Random.nextInt(1, range)
    return (0..size).map { i -> transform(i, size) }
}

private fun satelliteOf(
    parent: Body,
    distance: Distance,
    mass: Mass = smallMass(),
    radius: Distance = sizeOf(mass),
): InertialBody {
    val motion = getOrbitalMotion(mass, distance, parent)

    return InertialBody(
        id = uniqueID("satelliteOf(${parent.id})"),
        mass = mass,
        radius = radius,
        motion = motion,
    )
}

private fun anyDistance(min: Distance, max: Distance) =
    Random.nextFloat().mapTo(min.metres, max.metres).metres

private fun anyMass(): Mass = if (chance(5.percent)) largeMass() else smallMass()
private fun smallMass(): Mass = Random.nextInt(10, 30).kg
private fun largeMass(): Mass = Random.nextInt(50, 200).kg
private fun sizeOf(mass: Mass): Distance = maxOf(4f, (mass.kg * .25f)).metres
