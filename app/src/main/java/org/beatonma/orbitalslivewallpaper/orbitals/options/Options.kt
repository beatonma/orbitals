package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Paint
import org.beatonma.orbitalslivewallpaper.orbitals.mapTo
import org.beatonma.orbitalslivewallpaper.orbitals.physics.*
import kotlin.math.min
import kotlin.random.Random


data class Options(
    val frameRate: Int = 60,
    val physicsOptions: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
)

data class PhysicsOptions(
    val maxEntities: Int = 30,
    val systemGenerators: List<SystemGenerator> = listOf(
        SystemGenerator.StarSystem,
//        SystemGenerator.Randomized,
    ),
    val G: Float = 6.647f,
    val gravityMultiplier: Float = 1f,
    val collisionStyle: CollisionStyle = CollisionStyle.None,
)

data class VisualOptions(
    val focusCenterOfMass: Boolean = false,
    val showTraceLines: Boolean = false,
    val traceLineLength: Int = 25,
    val showAcceleration: Boolean = false,
    val drawStyle: DrawStyle = DrawStyle.Wireframe,
    val wireframe: Boolean = false,
    val colorOptions: ColorOptions = ColorOptions(),
)

enum class DrawStyle {
    Solid,
    Wireframe,
    ;

    fun setUp(paint: Paint) {
        when (this) {
            Solid -> paint.style = Paint.Style.FILL
            Wireframe -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
            }
        }
    }
}

enum class SystemGenerator {
    StarSystem,
    Randomized,
    ParticleGun,
    Polygonal,
    Cellular,
    ;

    fun generate(width: Int, height: Int): List<Body> = when (this) {
        StarSystem -> generateStarSystem(width, height)
        Randomized -> generateRandom(width, height)
        else -> listOf()
    }

    private fun generateStarSystem(width: Int, height: Int): List<Body> {
        val sun = FixedBody(
            label = "center",
            mass = largeMass(),
            radius = 50.metres,
            position = Position(.8f * width, .3f * height),
        )

        val minDistance = (min(width, height) * .1f).metres
        val maxDistance = (min(width, height) * .9f).metres

        return listOf(sun) + (1..Random.nextInt(2, 10)).map {
            satelliteOf(sun, anyDistance(minDistance, maxDistance))
        }
    }

    private fun generateRandom(width: Int, height: Int): List<Body> {
        return (1..Random.nextInt(2, 10)).map {
            InertialBody(
                label = "random[$it]",
                mass = anyMass(),
                motion = Motion(
                    Position(Random.nextInt(0, width), Random.nextInt(0, height)),
                    Velocity(Random.nextInt(0, 5), Random.nextInt(0, 5))
                )
            )
        }
    }
}

enum class CollisionStyle {
    None,
    Merge,
    ;
}

private fun satelliteOf(
    parent: Body,
    distance: Distance,
    mass: Mass = smallMass(),
    radius: Distance = sizeOf(mass),
): InertialBody {
    val motion = getOrbitalMotion(mass, distance, parent)

    return InertialBody(
        label = "satellite[${parent.label}]",
        mass = mass,
        radius = radius,
        motion = motion,
    )
}

private fun anyDistance(min: Distance, max: Distance) =
    Random.nextFloat().mapTo(min.metres, max.metres).metres

private fun anyMass(): Mass = if (Random.nextFloat() > .95f) largeMass() else smallMass()
private fun smallMass(): Mass = Random.nextInt(10, 500).kg
private fun largeMass(): Mass = Random.nextInt(800, 1500).kg

private fun sizeOf(mass: Mass): Distance = (mass.kg * .1f).metres
