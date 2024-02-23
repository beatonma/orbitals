package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import kotlin.random.Random

internal val GauntletGenerator = Generator { space, bodies, physics ->
    val randomBodies = RandomGenerator.run { invoke(space, bodies, physics) }

    randomBodies + createBodies(Random.nextInt(5)) { index, n ->
        createStar(
            "gauntlet",
            space.relativePosition(
                index * (1f / n),
                Random.nextFloat()
            ),
            physics.bodyDensity,
            asFixedBody = true,
        )
    }

}
