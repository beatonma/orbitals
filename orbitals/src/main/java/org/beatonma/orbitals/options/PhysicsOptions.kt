package org.beatonma.orbitals.options

data class PhysicsOptions(
    val maxEntities: Int = 15,
    val systemGenerators: List<SystemGenerator> = listOf(
        SystemGenerator.StarSystem,
        SystemGenerator.Randomized,
        SystemGenerator.Gauntlet,
    ),
    val G: Float = 6.647f,
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.None,
)

enum class CollisionStyle {
    None,
    Merge,
    ;
}
