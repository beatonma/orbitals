package org.beatonma.orbitals.engine

import org.beatonma.orbitals.chance
import org.beatonma.orbitals.mapTo
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.percent
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.Mass
import org.beatonma.orbitals.physics.Motion
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.Velocity
import org.beatonma.orbitals.physics.distanceTo
import org.beatonma.orbitals.physics.getOrbitalMotion
import org.beatonma.orbitals.physics.kg
import org.beatonma.orbitals.physics.metres
import org.beatonma.orbitals.physics.uniqueID
import kotlin.random.Random

private const val MaxFixedBodies = 3

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

    /**
     * Generate lots of small bodies around an existing large body.
     */
    Asteroids,
    ;

    fun generate(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        return if (space.isValid) when (this) {
            IdealStarSystem -> generateIdealStarSystem(space, bodies, physics)
            StarSystem -> generateStarSystem(space, bodies, physics)
            Randomized -> generateRandom(space, bodies, physics)
            Gauntlet -> generateGauntlet(space, bodies, physics)
            Asteroids -> generateAsteroids(space, bodies, physics)
        }
        else {
            println("Invalid space $space")
            listOf()
        }
    }

    private fun generateGauntlet(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        val fixedBodies = bodies.fixedBodies

        val randomBodies = generateRandom(space, bodies, physics)
        return if (fixedBodies.size > MaxFixedBodies) {
            randomBodies + createBodies(MaxFixedBodies - fixedBodies.size) { index, n ->
                val mass = largeMass()
                FixedBody(
                    id = uniqueID("gauntlet"),
                    mass = mass,
                    radius = sizeOf(mass),
                    motion = Motion(
                        space.relativePosition(
                            index * (1f / n),
                            Random.nextFloat()
                        )
                    )
                )
            }
        } else {
            randomBodies
        }
    }

    private fun generateIdealStarSystem(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        val fixedBodies = bodies.fixedBodies
        val useExistingStar = fixedBodies.isNotEmpty()

        val sun = if (useExistingStar) {
            fixedBodies.first()
        } else {
            val mass = 100.kg
            FixedBody(
                id = uniqueID("center"),
                mass = mass,
                radius = sizeOf(mass),
                motion = Motion(space.center),
            )
        }

        val satellites = listOf<Body>(
            satelliteOf(
                sun,
                space.radius.metres * .5f,
                1.kg,
                20.metres,
                G = physics.G,
            ),
            satelliteOf(
                sun,
                space.radius.metres * .3f,
                20.kg,
                20.metres,
                G = physics.G,
            )
        )

        return if (useExistingStar) {
            satellites
        } else {
            listOf(sun) + satellites
        }
    }

    /**
     * Try to find a random position that is at least [minDistance] away from any existing stars.
     */
    private fun generateStarPosition(
        space: Space,
        bodies: List<Body>,
        minDistance: Distance = 300.metres
    ): Position? {
        val existingStars = bodies.fixedBodies

        for (i in 0..5) {
            val candidatePosition = space.relativePosition(
                Random.nextFloat().mapTo(.2f, .8f),
                Random.nextFloat().mapTo(.2f, .8f)
            )

            if (existingStars.isEmpty()) return candidatePosition

            for (star in existingStars) {
                val distance = candidatePosition.distanceTo(star.position)
                if (distance < minDistance) {
                    break
                }
                else {
                    return candidatePosition
                }
            }
        }

        return null
    }

    private fun generateStarSystem(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        val fixedBodies = bodies.fixedBodies
        val useExistingStar = fixedBodies.size > 3

        val sun = if (useExistingStar) {
            fixedBodies.random()
        } else {
            val position = generateStarPosition(space, bodies) ?: return listOf()
            val mass = largeMass()
            FixedBody(
                id = uniqueID("center"),
                mass = mass,
                radius = sizeOf(mass),
                motion = Motion(position),
            )
        }

        val minDistance = (space.radius * .1f).metres
        val maxDistance = (space.radius * .9f).metres

        val satellites = createBodies(4) { _, _ ->
            satelliteOf(
                sun,
                distance = anyDistance(minDistance, maxDistance),
                G = physics.G,
            )
        }

        return if (useExistingStar) {
            satellites
        } else {
            listOf(sun) + satellites
        }
    }

    private fun generateRandom(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        return createBodies { index, _ ->
            val mass = anyMass()
            InertialBody(
                id = uniqueID("random[$index]"),
                mass = mass,
                radius = sizeOf(mass),
                motion = Motion(
                    space.relativePosition(Random.nextFloat(), Random.nextFloat()),
                    Velocity(Random.nextInt(-5, 5), Random.nextInt(-5, 5))
                )
            )
        }
    }

    private fun generateAsteroids(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        val sun = bodies.fixedBodies.randomOrNull() ?: return listOf()

        val distance = Random.nextInt(80, 100)

        return createBodies(10) { index, n ->
            satelliteOf(
                parent = sun,
                distance = anyDistance(distance.metres, (distance + 5).metres),
                mass = verySmallMass(),
                G = physics.G,
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
    G: Float,
): InertialBody =
    InertialBody(
        id = uniqueID("satelliteOf(${parent.id})"),
        mass = mass,
        radius = radius,
        motion = getOrbitalMotion(mass, distance, parent, G = G),
    )

private fun anyDistance(min: Distance, max: Distance) =
    Random.nextFloat().mapTo(min.value, max.value).metres

private fun anyMass(): Mass = when {
    chance(2.percent) -> verySmallMass()
    chance(5.percent) -> largeMass()
    else -> smallMass()
}

private fun verySmallMass(): Mass = Random.nextFloat().kg
private fun smallMass(): Mass = Random.nextInt(5, 20).kg
private fun largeMass(): Mass = Random.nextInt(50, 200).kg

private fun sizeOf(mass: Mass): Distance = maxOf(4f, (mass.value * .25f)).metres

private val List<Body>.fixedBodies: List<FixedBody>
    get() = filterIsInstance<FixedBody>()
