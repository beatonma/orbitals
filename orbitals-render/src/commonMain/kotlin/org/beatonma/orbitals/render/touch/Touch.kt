package org.beatonma.orbitals.render.touch

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Region
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.uniqueID
import org.beatonma.orbitals.render.OrbitalsRenderEngine


fun createTouchAttractor(position: Position, physics: PhysicsOptions) =
    GreatAttractor(
        id = uniqueID("touch"),
        mass = Config.getGreatAttractorMass(),
        density = physics.bodyDensity,
        motion = Motion(position),
    ).apply { isMortal = false }

fun getTouchRegion(position: Position): Region {
    val x = position.x.value.toInt()
    val y = position.y.value.toInt()

    return Region(
        x - 250,
        y - 250,
        x + 250,
        y + 250
    )
}

fun <Key, Canvas> clearTouchBodies(
    orbitals: OrbitalsRenderEngine<Canvas>,
    bodyMap: MutableMap<Key, UniqueID>
) {
    val bodies = bodyMap.values.toList()
    bodyMap.clear()
    orbitals.remove(bodies)
}
