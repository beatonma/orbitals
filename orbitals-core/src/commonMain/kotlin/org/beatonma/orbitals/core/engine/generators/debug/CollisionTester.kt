package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.kg

internal val CollisionTestGenerator = Generator { space, bodies, physics ->
    listOf(
        InertialBody(
            mass = 100.kg,
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(.4f, .5f),
                Velocity(1f, 0f)
            )
        ),
        InertialBody(
            mass = 100.kg,
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(.6f, .5f),
                Velocity(-1f, 0f)
            )
        )
    )
}
