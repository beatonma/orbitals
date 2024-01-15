package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.kg
import kotlin.time.Duration.Companion.milliseconds

object Config {
    val MinObjectMass = 1.kg
    val MaxObjectMass = 1000.kg
    val CollisionMinimumAge = 250.milliseconds
}
