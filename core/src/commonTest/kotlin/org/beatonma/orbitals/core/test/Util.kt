package org.beatonma.orbitals.core.test

import org.beatonma.orbitals.core.physics.BodyState
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.ZeroPosition
import org.beatonma.orbitals.core.physics.ZeroVelocity
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.sizeOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

const val DefaultTestG = 6.674f
val DefaultTestDensity = Density(1f)
val DefaultTestMass = 100.kg
val DefaultColliderAge = 10.seconds


fun inertialBody(
    mass: Mass = DefaultTestMass,
    density: Density = DefaultTestDensity,
    position: Position = ZeroPosition,
    velocity: Velocity = ZeroVelocity,
    motion: Motion = Motion(position, velocity),
    radius: Distance = sizeOf(mass, density),
    age: Duration = DefaultColliderAge,
    state: BodyState = BodyState.MainSequence,
) = InertialBody(
    mass,
    density,
    motion,
    radius,
    age = age,
    state = state,
)

fun fixedBody(
    mass: Mass = DefaultTestMass,
    density: Density = DefaultTestDensity,
    position: Position = ZeroPosition,
    motion: Motion = Motion(position, ZeroVelocity),
    radius: Distance = sizeOf(mass, density),
    age: Duration = DefaultColliderAge,
    state: BodyState = BodyState.MainSequence,
) = FixedBody(
    mass,
    density,
    motion,
    radius,
    age = age,
    state = state,
)

fun greatAttractor(
    mass: Mass = DefaultTestMass,
    density: Density = DefaultTestDensity,
    position: Position = ZeroPosition,
    motion: Motion = Motion(position, ZeroVelocity),
    radius: Distance = sizeOf(mass, density),
    age: Duration = DefaultColliderAge,
    state: BodyState = BodyState.MainSequence,
) = GreatAttractor(
    mass,
    density,
    motion,
    radius,
    age = age,
    state = state,
)
