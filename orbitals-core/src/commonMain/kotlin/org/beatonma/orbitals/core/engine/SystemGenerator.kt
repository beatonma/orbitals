package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.mapTo
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.ZeroVelocity
import org.beatonma.orbitals.core.physics.distanceTo
import org.beatonma.orbitals.core.physics.getOrbitalMotion
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.physics.uniqueID
import kotlin.math.max
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

    /**
     * Generate a large mass beyond the edge of our visible 'universe'.
     */
    GreatAttractor,

    CollisionTester,
    ;

    fun generate(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        if (!space.isValid) {
            println("Invalid space $space")
            return listOf()
        }

        val f = when (this) {
            IdealStarSystem -> ::generateIdealStarSystem
            StarSystem -> ::generateStarSystem
            Randomized -> ::generateRandom
            Gauntlet -> ::generateGauntlet
            Asteroids -> ::generateAsteroids
            GreatAttractor -> ::generateGreatAttractor
            CollisionTester -> ::generatorCollisionTester
        }

        return f(space, bodies, physics)
    }

    private fun generatorCollisionTester(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> = listOf(
        InertialBody(
            mass = 100.kg,
            motion = Motion(
                space.relativePosition(.4f, .5f),
                Velocity(1f, 0f)
            )
        ),
        InertialBody(
            mass = 100.kg,
            motion = Motion(
                space.relativePosition(.6f, .5f),
                Velocity(-1f, 0f)
            )
        )
    )

    private fun generateGreatAttractor(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        return listOf(
            GreatAttractor(
                mass = attractorMass(),
                motion = Motion(
                    space.relativePosition(direction() * 2f, direction() * 2f)
                )
            )
        )
    }

    private fun generateGauntlet(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        val fixedBodies = bodies.fixedBodies

        val randomBodies = generateRandom(space, bodies, physics)
        return if (fixedBodies.size > MaxFixedBodies) {
            randomBodies + createBodies(MaxFixedBodies - fixedBodies.size) { index, n ->
                createStar(
                    "gauntlet",
                    space.relativePosition(
                        index * (1f / n),
                        Random.nextFloat()
                    ),
                    asFixedBody = true,
                )
            }
        } else {
            randomBodies
        }
    }

    private fun generateIdealStarSystem(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        val fixedBodies = bodies.fixedBodies
        val useExistingStar = fixedBodies.isNotEmpty()

        val sun = if (useExistingStar) {
            fixedBodies.first()
        } else {
            createStar("center", space.center)
        }

        val satellites = listOf<Body>(
            satelliteOf(
                sun,
                space.radius.metres * .5f,
                1.kg,
                G = physics.G,
            ),
            satelliteOf(
                sun,
                space.radius.metres * .3f,
                20.kg,
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
                } else {
                    return candidatePosition
                }
            }
        }

        return null
    }

    private fun generateStarSystem(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        val fixedBodies = bodies.fixedBodies
        val useExistingStar = fixedBodies.size > 3

        val sun = if (useExistingStar) {
            fixedBodies.random()
        } else {
            val position = generateStarPosition(space, bodies) ?: return listOf()
            createStar("center", position)
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

    private fun generateRandom(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        return createBodies { index, _ ->
            val mass = anyMass()
            InertialBody(
                id = uniqueID("random[$index]"),
                mass = mass,
                motion = Motion(
                    space.relativePosition(Random.nextFloat(), Random.nextFloat()),
                    Velocity(Random.nextInt(-5, 5), Random.nextInt(-5, 5))
                )
            )
        }
    }

    private fun generateAsteroids(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body> {
        val sun = bodies.fixedBodies.randomOrNull() ?: return listOf()

        val distance = Random.nextInt(80, 100)

        return createBodies(10) { index, n ->
            satelliteOf(
                parent = sun,
                distance = anyDistance(distance.metres, (distance + 5).metres),
                mass = asteroidMass(),
                G = physics.G,
            )
        }
    }
}

private fun createBodies(range: Int = 10, transform: (index: Int, n: Int) -> Body): List<Body> {
    val size = Random.nextInt(1, max(range, 2))
    return (0..size).map { i -> transform(i, size) }
}

private fun createStar(
    name: String,
    position: Position,
    mass: Mass = starMass(),
    velocity: Velocity = ZeroVelocity,
    asFixedBody: Boolean = chance(10.percent),
): Body {
    return if (asFixedBody) {
        FixedBody(
            id = uniqueID(name),
            mass = mass,
            motion = Motion(position, velocity),
        )
    } else {
        InertialBody(
            id = uniqueID(name),
            mass = mass,
            motion = Motion(position, velocity),
        )
    }
}

private fun satelliteOf(
    parent: Body,
    distance: Distance,
    mass: Mass = planetMass(),
    G: Float,
): InertialBody =
    InertialBody(
        id = uniqueID("satelliteOf(${parent.id})"),
        mass = mass,
        motion = getOrbitalMotion(mass, distance, parent, G = G),
    )

private fun anyDistance(min: Distance, max: Distance) =
    Random.nextFloat().mapTo(min.value, max.value).metres

private fun anyMass(): Mass = when {
    chance(2.percent) -> asteroidMass()
    chance(5.percent) -> starMass()
    else -> planetMass()
}

private fun asteroidMass(): Mass = Random.nextFloat().kg
private fun planetMass(): Mass = Random.nextInt(5, 20).kg
private fun starMass(): Mass = Random.nextInt(50, 200).kg
private fun attractorMass(): Mass = Random.nextInt(2500, 5000).kg

private val List<Body>.fixedBodies: List<FixedBody>
    get() = filterIsInstance<FixedBody>()


private fun direction(): Int = if (chance(50.percent)) -1 else 1
