package org.beatonma.orbitals.core.physics

import kotlin.time.Duration

class Motion(
    position: Position = ZeroPosition,
    velocity: Velocity = ZeroVelocity,
) {
    var position: Position = position
        internal set
    var velocity: Velocity = velocity
        internal set

    var acceleration: Acceleration = ZeroAcceleration
        internal set

    override fun toString(): String {
        return "Motion($position, $velocity)"
    }

    /**
     * Warning: With large [timeDelta], position will move in long straight lines
     */
    fun applyInertia(timeDelta: Duration) {
        position.x += velocity.x * timeDelta
        position.y += velocity.y * timeDelta
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + velocity.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Motion

        if (position != other.position) return false
        if (velocity != other.velocity) return false

        return true
    }
}
