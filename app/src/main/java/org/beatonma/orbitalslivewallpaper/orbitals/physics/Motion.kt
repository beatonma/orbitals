package org.beatonma.orbitalslivewallpaper.orbitals.physics

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class Motion(
    private var startPosition: Position = ZeroPosition,
    private var startVelocity: Velocity = ZeroVelocity,
) {
    val position: Position get() = startPosition
    val velocity: Velocity get() = startVelocity

    var accelerationDelta: AccelerationDelta? = null
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

fun staticPosition(x: Number, y: Number) =
    Motion(
        Position(x, y),
        Velocity(0, 0),
    )

fun staticPosition(x: Distance, y: Distance) =
    Motion(
        Position(x, y),
        Velocity(0, 0),
    )
