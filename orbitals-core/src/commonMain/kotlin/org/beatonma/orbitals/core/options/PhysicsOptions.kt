package org.beatonma.orbitals.core.options

import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.physics.DefaultG
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class PhysicsOptions(
    val autoAddBodies: Boolean = true,
    val maxFixedBodyAge: Duration = 1.minutes,
    val maxEntities: Int = 50,
    val systemGenerators: Set<SystemGenerator> = setOf(
        SystemGenerator.StarSystem,
    ),
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.None,
) {
    val G = DefaultG * gravityMultiplier
}
