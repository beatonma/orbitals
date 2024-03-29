package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.GeneratorScope
import org.beatonma.orbitals.core.engine.Space
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.mapTo
import org.beatonma.orbitals.core.nextFloat
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.distanceTo
import org.beatonma.orbitals.core.physics.metres
import kotlin.random.Random

internal val StarSystemGenerator = Generator { space, bodies, physics ->
    val fixedBodies = bodies.fixedBodies
    val useExistingStar = fixedBodies.size > 3

    val sun = when {
        useExistingStar -> fixedBodies.random()
        else -> {
            val position = generateStarPosition(space, bodies) ?: return@Generator listOf()
            createStar("center", position, physics.bodyDensity)
        }
    }

    val minDistance = space.radius * .1f
    val maxDistance = space.radius * .9f

    val satellites = createBodies(4) { _, _ ->
        satelliteOf(
            sun,
            distance = Random.nextFloat(minDistance, maxDistance).metres,
            density = physics.bodyDensity,
            G = physics.G,
        )
    }

    when {
        useExistingStar -> satellites
        else -> listOf(sun) + satellites
    }
}


/**
 * Try to find a random position that is at least [minDistance] away from any existing stars.
 */
private fun GeneratorScope.generateStarPosition(
    space: Space,
    bodies: List<Body>,
    minDistance: Distance = Config.MinStarDistance,
): Position? {
    val existingStars = bodies.fixedBodies

    for (i in 0..5) {
        val candidatePosition = space.relativePosition(
            Random.nextFloat().mapTo(.2f, .8f),
            Random.nextFloat().mapTo(.2f, .8f)
        )

        if (existingStars.isEmpty()) return candidatePosition

        var acceptable = true
        for (star in existingStars) {
            val distance = candidatePosition.distanceTo(star.position)
            if (distance < minDistance) {
                acceptable = false
                break
            }
        }
        if (acceptable) return candidatePosition
    }

    return null
}
