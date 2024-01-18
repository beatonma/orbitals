package org.beatonma.orbitals.core.options

enum class CollisionStyle {
    /**
     * No collision behaviour - objects can pass through one another unchanged.
     */
    None,

    /**
     * Larger objects absorb mass from smaller colliders.
     */
    Merge,

    /**
     * Collisions cause ejecta - bits of the colliding bodies break away.
     */
    Break,

    /**
     * Bodies are bouncy.
     */
    Bouncy,

    /**
     * Bodies are sticky.
     */
    Sticky,
    ;
}
