package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.chance
import org.beatonma.orbitals.core.engine.generators.AsteroidGenerator
import org.beatonma.orbitals.core.engine.generators.GauntletGenerator
import org.beatonma.orbitals.core.engine.generators.GreatAttractorGenerator
import org.beatonma.orbitals.core.engine.generators.IdealStarSystemGenerator
import org.beatonma.orbitals.core.engine.generators.RandomGenerator
import org.beatonma.orbitals.core.engine.generators.StarSystemGenerator
import org.beatonma.orbitals.core.engine.generators.debug.CollisionTestGenerator
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.percent
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.FixedBody
import org.beatonma.orbitals.core.physics.InertialBody
import org.beatonma.orbitals.core.physics.Mass
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.Velocity
import org.beatonma.orbitals.core.physics.ZeroVelocity
import org.beatonma.orbitals.core.physics.getOrbitalMotion
import org.beatonma.orbitals.core.physics.uniqueID
import org.beatonma.orbitals.core.util.debug
import kotlin.math.max
import kotlin.random.Random


internal fun interface Generator {
    operator fun GeneratorScope.invoke(
        space: Space,
        bodies: List<Body>,
        physics: PhysicsOptions
    ): List<Body>
}

internal object GeneratorScope {
    fun createBodies(range: Int = 10, transform: (index: Int, n: Int) -> Body): List<Body> {
        val size = Random.nextInt(1, max(range, 2))
        return (0..size).map { i -> transform(i, size) }
    }

    fun createStar(
        name: String,
        position: Position,
        density: Density,
        mass: Mass = Config.getStarMass(),
        velocity: Velocity = ZeroVelocity,
        asFixedBody: Boolean = chance(10.percent),
    ): Body {
        return if (asFixedBody) {
            FixedBody(
                id = uniqueID(name),
                mass = mass,
                density = density,
                motion = Motion(position, velocity),
            )
        } else {
            InertialBody(
                id = uniqueID(name),
                mass = mass,
                density = density,
                motion = Motion(position, velocity),
            )
        }
    }

    fun satelliteOf(
        parent: Body,
        distance: Distance,
        mass: Mass = Config.getPlanetMass(),
        density: Density,
        G: Float,
    ): InertialBody =
        InertialBody(
            id = uniqueID("satelliteOf(${parent.id})"),
            mass = mass,
            density = density,
            motion = getOrbitalMotion(mass, distance, parent, G = G),
        )

    fun anyMass(): Mass = when {
        chance(2.percent) -> Config.getAsteroidMass()
        chance(5.percent) -> Config.getStarMass()
        else -> Config.getPlanetMass()
    }

    val List<Body>.fixedBodies: List<FixedBody>
        get() = filterIsInstance<FixedBody>()
}

enum class SystemGenerator(private val generator: Generator) {
    /**
     * Generate a simple StarSystem in the center of the given Space with a few satellites of fixed
     * definition.
     */
    IdealStarSystem(IdealStarSystemGenerator),

    /**
     * Generate a system of satellites orbiting a central star.
     */
    StarSystem(StarSystemGenerator),

    /**
     * Generate [InertialBody]s with random position, mass, size, velocity.
     */
    Randomized(RandomGenerator),

    /**
     * Generate a series of [FixedBody]s for bodies to navigate through.
     */
    Gauntlet(GauntletGenerator),

    /**
     * Generate lots of small bodies around an existing large body.
     */
    Asteroids(AsteroidGenerator),

    /**
     * Generate a large mass beyond the edge of our visible 'universe'.
     */
    GreatAttractor(GreatAttractorGenerator),

    DebugCollisionTester(CollisionTestGenerator),
    ;

    fun generate(space: Space, bodies: List<Body>, physics: PhysicsOptions): List<Body> {
        if (!space.isValid) {
            debug("Invalid space $space")
            return listOf()
        }

        return generator.run {
            GeneratorScope(space, bodies, physics)
        }
    }
}
