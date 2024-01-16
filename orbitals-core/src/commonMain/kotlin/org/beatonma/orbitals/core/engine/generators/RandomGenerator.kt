package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.uniqueID
import kotlin.random.Random

internal val RandomGenerator = Generator { space, _, physics ->
    createBodies { index, _ ->
        val mass = anyMass()
        InertialBody(
            id = uniqueID("random[$index]"),
            mass = mass,
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(Random.nextFloat(), Random.nextFloat()),
                Config.getVelocity()
            )
        )
    }
}
