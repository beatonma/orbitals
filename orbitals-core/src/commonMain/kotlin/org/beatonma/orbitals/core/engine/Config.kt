package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Speed
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.randomDirection
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Config {
    // Multiplier to greatly exaggerate the real value of G.
    const val GravityScale = 1e12f

    // Minimum distance used in gravity calculations between bodies.
    val MinGravityDistance = 30.metres

    // Minimum age for a body to take part in collisions.
    // Allows a grace period for newly-created objects to move away
    // from each other without immediately colliding.
    val CollisionMinimumAge: Duration = 250.milliseconds

    // Objects that get too small will be removed from the simulation.
    val MinObjectMass: Mass = 1f.kg

    // Objects that get too big will be removed from the simulation.
    val MaxObjectMass: Mass = 20_000f.kg
    val DefaultDensity: Density = Density(1f)

    // Generated object mass ranges
    fun getAsteroidMass(): Mass = Random.nextInt(1, 80).kg
    fun getPlanetMass(): Mass = Random.nextInt(100..750).kg
    fun getStarMass(): Mass = Random.nextInt(1_000..3_000).kg
    fun getGreatAttractorMass(): Mass = Random.nextInt(7_500..10_000).kg

    // Generated object distance ranges
    fun getAsteroidDistance(): Distance = Random.nextInt(80, 150).metres

    // Minimum distance from existing stars to generate a new star.
    val MinStarDistance: Distance = 300f.metres

    // Generated object velocity ranges
    private fun getSpeed(): Speed = Speed(Random.nextInt(20) * randomDirection)
    fun getVelocity(): Velocity = Velocity(getSpeed(), getSpeed())
}
