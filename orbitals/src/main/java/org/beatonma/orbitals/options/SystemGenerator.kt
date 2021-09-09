package org.beatonma.orbitals.options

import org.beatonma.orbitals.Space
import org.beatonma.orbitals.mapTo
import org.beatonma.orbitals.physics.*
import org.beatonma.orbitals.relativeVisiblePosition
import kotlin.random.Random

enum class SystemGenerator {
    StarSystem,
    Randomized,
    Gauntlet,
    ParticleGun,
    Polygonal,
    Cellular,
    ;

    fun generate(space: Space): List<Body> = when (this) {
        StarSystem -> generateStarSystem(space)
        Randomized -> generateRandom(space)
        Gauntlet -> gauntlet(space)
        else -> listOf()
    }

    private fun gauntlet(space: Space): List<Body> {
        return createBodies(4) { index, n ->
            val mass = largeMass()
            FixedBody(
                id = uniqueID("gauntlet"),
                mass = mass,
                radius = sizeOf(mass),
                position = space.relativeVisiblePosition(
                    index * (1f / n),
                    Random.nextFloat()
                )
            )
        } + generateRandom(space)
    }

    private fun generateStarSystem(space: Space): List<Body> {
        val mass = largeMass()
        val sun = FixedBody(
            id = uniqueID("center"),
            mass = mass,
            radius = sizeOf(mass),
            position = space.relativeVisiblePosition(Random.nextFloat(), Random.nextFloat()),
        )

        val minDistance = (space.radius * .1f).metres
        val maxDistance = (space.radius * .9f).metres

        return listOf(sun) + createBodies { _, _ ->
            satelliteOf(sun, anyDistance(minDistance, maxDistance))
        }
    }

    private fun generateRandom(space: Space): List<Body> {
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

private fun anyMass(): Mass = if (Random.nextFloat() > .95f) largeMass() else smallMass()
private fun smallMass(): Mass = Random.nextInt(10, 30).kg
private fun largeMass(): Mass = Random.nextInt(50, 200).kg
private fun sizeOf(mass: Mass): Distance = maxOf(4f, (mass.kg * .25f)).metres
