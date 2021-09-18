package org.beatonma.orbitals.options

import org.beatonma.orbitals.engine.SystemGenerator
import org.beatonma.orbitals.physics.DefaultG
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class PhysicsOptions @OptIn(ExperimentalTime::class) constructor(
    val autoAddBodies: Boolean = true,
    val maxFixedBodyAgeMinutes: Duration = Duration.minutes(1),
    val maxEntities: Int = 50,
    val systemGenerators: Set<SystemGenerator> = setOf(
        SystemGenerator.StarSystem,
    ),
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.None,
    val tickDelta: Duration = Duration.seconds(1)
) {
    val G = DefaultG * gravityMultiplier
}

enum class CollisionStyle {
    None,
    Break,
    Merge,
    ;
}
