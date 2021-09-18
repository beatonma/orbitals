package org.beatonma.orbitals.rendering.touch

import org.beatonma.orbitals.engine.Region
import org.beatonma.orbitals.physics.GreatAttractor
import org.beatonma.orbitals.physics.Motion
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitals.physics.kg
import org.beatonma.orbitals.physics.uniqueID
import org.beatonma.orbitals.rendering.OrbitalsRenderEngine

fun createTouchAttractor(position: Position) =
    GreatAttractor(
        id = uniqueID("touch"),
        mass = 10_000.kg,
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
    println("clearTouchBodies")
    val bodies = bodyMap.values.toList()
    bodyMap.clear()
    bodies.forEach { orbitals.removeBody(it) }
}
