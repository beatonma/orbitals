package org.beatonma.orbitals.core.engine.generators.debug

import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.angleTo
import org.beatonma.orbitals.core.physics.metresPerSecond
import kotlin.random.Random

internal val CollisionTestGenerator = Generator { space, bodies, physics ->
    val firstPosition = space.relativePosition(position(), position())
    val secondPosition = space.relativePosition(position(), position())
    val speed = 50f.metresPerSecond

    listOf(
        createStar(
            "collision-test",
            position = firstPosition,
            density = physics.bodyDensity,
            velocity = Velocity(speed, firstPosition.angleTo(secondPosition)),
        ),
        createStar(
            "collision-test",
            position = secondPosition,
            density = physics.bodyDensity,
            velocity = Velocity(speed, secondPosition.angleTo(firstPosition)),
        ),
    )
}

private fun position() = (Random.nextFloat() * .5f) + .25f
