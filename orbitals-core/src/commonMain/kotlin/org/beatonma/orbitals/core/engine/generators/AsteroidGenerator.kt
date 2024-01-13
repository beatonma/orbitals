package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.physics.metres
import kotlin.random.Random

internal val AsteroidGenerator = Generator { _, bodies, physics ->
    val sun = bodies.randomOrNull() ?: return@Generator listOf()

    val distance = Random.nextInt(80, 100)

    createBodies(10) { _, _ ->
        satelliteOf(
            parent = sun,
            distance = anyDistance(
                distance.metres,
                (distance + 5).metres
            ),
            mass = asteroidMass(),
            density = physics.bodyDensity,
            G = physics.G,
        )
    }
}
