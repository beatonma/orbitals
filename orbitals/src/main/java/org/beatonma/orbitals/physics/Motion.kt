package org.beatonma.orbitals.physics

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class Motion internal constructor(
    private var startPosition: Position = ZeroPosition,
    private var startVelocity: Velocity = ZeroVelocity,
) {
    val position: Position get() = startPosition
    val velocity: Velocity get() = startVelocity

    var acceleration: Acceleration = ZeroAcceleration
        internal set

    /**
     * Warning: With large [timeDelta], position will move in long straight lines
     */
    @OptIn(ExperimentalTime::class)
    fun applyInertia(timeDelta: Duration) {
        position.x += velocity.x * timeDelta
        position.y += velocity.y * timeDelta
    }
}
