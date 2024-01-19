package org.beatonma.orbitals.core.options

import org.beatonma.orbitals.core.Platform
import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.physics.DefaultG
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.platform
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class PhysicsOptions(
    val autoAddBodies: Boolean = true,
    val maxFixedBodyAge: Duration = 30.seconds,
    val maxEntities: Int = when (platform) {
        Platform.Web -> 30
        else -> 50
    },
    val systemGenerators: Set<SystemGenerator> = setOf(
        SystemGenerator.Asteroids,
        SystemGenerator.Randomized,
        SystemGenerator.StarSystem,
    ),
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.Merge,
    val bodyDensity: Density = Config.DefaultDensity,
) {
    val G = DefaultG * gravityMultiplier
}
