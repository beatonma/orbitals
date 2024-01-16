package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metresPerSecond
import kotlin.random.Random

internal val CollisionTestGenerator = Generator { space, bodies, physics ->
    val firstPosition = space.relativePosition(position(), position())
    val secondPosition = space.relativePosition(position(), position())
    val speed = 50f.metresPerSecond

    listOf(
        InertialBody(
            mass = 100.kg,
            density = physics.bodyDensity,
            motion = Motion(
                firstPosition,
                Velocity(speed, firstPosition.angleTo(secondPosition))
            )
        ),
        InertialBody(
            mass = 60.kg,
            density = physics.bodyDensity,
            motion = Motion(
                secondPosition,
                Velocity(speed, secondPosition.angleTo(firstPosition))
            )
        )
    )
}

private fun position() = (Random.nextFloat() * .5f) + .25f
