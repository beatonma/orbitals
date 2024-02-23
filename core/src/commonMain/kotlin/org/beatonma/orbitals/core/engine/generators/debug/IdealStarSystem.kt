package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.metres


internal val IdealStarSystemGenerator = Generator { space, bodies, physics ->
    val fixedBodies = bodies.fixedBodies
    val useExistingStar = fixedBodies.isNotEmpty()

    val sun = when {
        useExistingStar -> fixedBodies.first()
        else -> createStar("center", space.center, physics.bodyDensity)
    }

    val satellites = listOf<Body>(
        satelliteOf(
            sun,
            (space.radius * .5f).metres,
            Config.getAsteroidMass(),
            physics.bodyDensity,
            G = physics.G,
        ),
        satelliteOf(
            sun,
            (space.radius * .3f).metres,
            Config.getPlanetMass(),
            physics.bodyDensity,
            G = physics.G,
        )
    )

    when {
        useExistingStar -> satellites
        else -> listOf(sun) + satellites
    }
}
