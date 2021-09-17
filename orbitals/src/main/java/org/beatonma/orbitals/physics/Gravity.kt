package org.beatonma.orbitals.physics

import org.beatonma.orbitals.chance
import org.beatonma.orbitals.percent
import kotlin.math.sqrt
import kotlin.random.Random

private const val SCALE = 1e14f

/**
 * Units: m^3 kg^-1 s^-2
 */
private const val GRAVITATIONAL_CONSTANT_ACTUAL = 6.674e-11f
internal const val DefaultG = GRAVITATIONAL_CONSTANT_ACTUAL * SCALE


fun calculateGravitationalForce(
    mass: Mass,
    otherMass: Mass,
    distance: Distance,
    minDistance: Distance = 30.metres, // Force a minimum distance to avoid extreme behaviour.
    G: Float,
): Force {
    val distanceSquared = distance
        .coerceAtLeast(minDistance)
        .squared

    val m = mass * otherMass

    return ((G * m) / distanceSquared).newtons
}

fun getOrbitalMotion(
    mass: Mass,
    distance: Distance,
    parent: Body,
    radialAngle: Angle = Random.nextInt(0, 359).degrees,
    prograde: Boolean = chance(.995f), // Small chance of retrograde orbit
    G: Float,
): Motion {
    val position = getRadialPosition(parent.position, distance, radialAngle)
    val speed = getOrbitalSpeed(mass, parent.mass, distance, G)
    val tangentialAngle = getTangentialAngle(radialAngle, prograde)

    return Motion(
        position,
        getVelocity(speed, tangentialAngle) + parent.velocity
    )
}

fun getOrbitalMotion(
    mass: Mass,
    position: Position,
    parent: Body,
    prograde: Boolean = chance(.995f.percent), // Small chance of retrograde orbit
    G: Float,
): Motion {
    val radialAngle = position.angleTo(parent.position)
    val distance = position.distanceTo(parent.position)
    val speed = getOrbitalSpeed(mass, parent.mass, distance, G)

    val tangentialAngle = getTangentialAngle(radialAngle, prograde)

    return Motion(
        position,
        getVelocity(speed, tangentialAngle)
    )
}

private fun getOrbitalSpeed(
    firstMass: Mass,
    secondMass: Mass,
    distance: Distance,
    G: Float,
): Speed =
    sqrt(
        (G * (firstMass + secondMass).value / distance.value)
    ).metres.perSecond

private fun getVelocity(speed: Speed, tangentialAngle: Angle) = Velocity(
    speed * cos(tangentialAngle),
    speed * sin(tangentialAngle)
)

private fun getTangentialAngle(radialAngle: Angle, prograde: Boolean): Angle {
    val direction = if (prograde) 1 else -1
    return radialAngle + (90 * direction).degrees
}
