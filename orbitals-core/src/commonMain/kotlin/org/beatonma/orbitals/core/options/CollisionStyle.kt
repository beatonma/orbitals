package org.beatonma.orbitals.core.options

enum class CollisionStyle {
    /**
     * No collision behaviour - objects can pass through one another unchanged.
     */
    None,

    /**
     * Collisions cause ejecta - bits of the colliding bodies break away.
     */
    Break,

    /**
     * Larger objects absorb mass from smaller colliders.
     */
    Merge,

    /**
     * Bodies are forbidden from overlapping.
     */
    Bounce,
    ;
}
