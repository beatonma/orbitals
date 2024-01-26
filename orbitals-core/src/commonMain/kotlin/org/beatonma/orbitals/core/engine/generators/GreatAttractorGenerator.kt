package org.beatonma.orbitals.core.engine.generators

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Generator
import org.beatonma.orbitals.core.engine.relativePosition
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.randomDirection


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
internal val GreatAttractorGenerator = Generator { space, bodies, physics ->
    listOf(
        GreatAttractor(
            mass = Config.getGreatAttractorMass(),
            density = physics.bodyDensity,
            motion = Motion(
                space.relativePosition(
                    position(),
                    position()
                )
            )
        )
    )
}

private fun position(): Float {
    val where = randomDirection(Config.UniverseOverflow / 2f)
    return when {
        where < 0f -> where
        else -> 1f + where
    }
}
