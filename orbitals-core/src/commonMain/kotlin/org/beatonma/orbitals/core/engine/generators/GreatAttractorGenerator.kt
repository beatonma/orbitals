package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.randomDirection


internal val GreatAttractorGenerator = Generator { space, bodies, physics ->
    listOf(
        GreatAttractor(
            mass = Config.getGreatAttractorMass(),
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(randomDirection(2f), randomDirection(2f))
            )
        )
    )
}
