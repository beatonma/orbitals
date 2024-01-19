package org.beatonma.orbitals.render.touch

import org.beatonma.orbitals.core.engine.Region
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.kg
import org.beatonma.orbitals.core.physics.uniqueID
import org.beatonma.orbitals.render.OrbitalsRenderEngine

fun createTouchAttractor(position: Position) =
    GreatAttractor(
        id = uniqueID("touch"),
        mass = 5_000f.kg,
        density = Density(1f),
        motion = Motion(position),
    )

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
