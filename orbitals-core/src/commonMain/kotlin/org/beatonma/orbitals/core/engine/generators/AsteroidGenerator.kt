package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator

internal val AsteroidGenerator = Generator { _, bodies, physics ->
    val sun = bodies.randomOrNull() ?: return@Generator listOf()

    createBodies(10) { _, _ ->
        satelliteOf(
            parent = sun,
            distance = Config.getAsteroidDistance(),
            mass = Config.getAsteroidMass(),
            density = physics.bodyDensity,
            G = physics.G,
        )
    }
}
