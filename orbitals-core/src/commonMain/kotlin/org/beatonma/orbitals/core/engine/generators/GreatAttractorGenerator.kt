package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion


internal val GreatAttractorGenerator = Generator { space, bodies, physics ->
    listOf(
        GreatAttractor(
            mass = attractorMass(),
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(direction() * 2f, direction() * 2f)
            )
        )
    )
}
