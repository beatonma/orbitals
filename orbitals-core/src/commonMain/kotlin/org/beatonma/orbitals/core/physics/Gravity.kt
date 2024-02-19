package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.nextFloat
import org.beatonma.orbitals.core.percent
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import kotlin.random.Random


/**
 * Units: m^3 kg^-1 s^-2
 */
private const val GRAVITATIONAL_CONSTANT_ACTUAL = 6.674e-11f
internal const val DefaultG = GRAVITATIONAL_CONSTANT_ACTUAL * Config.GravityScale


fun calculateGravitationalForce(
    mass: Mass,
    otherMass: Mass,
    distance: Distance,
    minDistance: Distance = Config.MinGravityDistance, // Force a minimum distance to avoid extreme behaviour.
    G: Float,
): Force {
    val distanceSquared = distance
        .coerceAtLeast(minDistance)
        .squared.value

    val m = mass * otherMass

    return ((G * m) / distanceSquared).newtons
}

fun getOrbitalMotion(
    mass: Mass,
    distance: Distance,
    parent: Body,
    radialAngle: Angle = Random.nextFloat(0f, 359f).degrees,
    prograde: Boolean = chance(.995f), // Small chance of retrograde orbit
    G: Float,
): Motion {
    val position = getRadialPosition(parent.position, distance, radialAngle)
    val speed = getOrbitalSpeed(mass, parent.mass, distance, G)
    val tangentialAngle = getTangentialAngle(radialAngle, prograde)

    return Motion(
        position,
        Velocity(speed, tangentialAngle) + parent.velocity
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
        Velocity(speed, tangentialAngle)
    )
}

fun getEscapeSpeed(
    mass: Mass,
    distance: Distance,
    G: Float,
): Speed {
    val g = G * mass.value / distance.squared.value
    return sqrt(2f * g * distance.value).metresPerSecond
}

private fun getOrbitalSpeed(
    firstMass: Mass,
    secondMass: Mass,
    distance: Distance,
    G: Float,
): Speed =
    sqrt(
        (G * (firstMass + secondMass).value / distance.value).absoluteValue // G may be negative
    ).metresPerSecond

private fun getTangentialAngle(radialAngle: Angle, prograde: Boolean): Angle {
    val direction = if (prograde) 1 else -1
    return radialAngle + (90f * direction).degrees
}
