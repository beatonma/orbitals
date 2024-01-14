package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.kg
import kotlin.random.Random

internal val CollisionTestGenerator = Generator { space, bodies, physics ->
    listOf(
        InertialBody(
            mass = 100.kg,
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(position(), position()),
            )
        ),
        InertialBody(
            mass = 60.kg,
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(position(), position()),
            )
        )
    )
}

private fun position() = (Random.nextFloat() * .5f) + .25f
