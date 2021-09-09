package org.beatonma.orbitals.options

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class PhysicsOptions @OptIn(ExperimentalTime::class) constructor(
    val maxEntities: Int = 50,
    val systemGenerators: List<SystemGenerator> = listOf(
        SystemGenerator.StarSystem,
        SystemGenerator.Randomized,
        SystemGenerator.Gauntlet,
    ),
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.None,
    val tickDelta: Duration = Duration.seconds(1)
) {
    val G = 6.647f * gravityMultiplier
}

enum class CollisionStyle {
    None,
    Merge,
    ;
}
