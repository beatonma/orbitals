package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.kg


internal val SupernovaGenerator = Generator { space, bodies, physics ->
    listOf(
        InertialBody(
            mass = Config.MaxObjectMass + 1f.kg,
            density = physics.bodyDensity,
            motion = Motion(space.center),
        )
    )
}
