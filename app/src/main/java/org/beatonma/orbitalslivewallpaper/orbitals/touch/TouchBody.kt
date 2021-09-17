package org.beatonma.orbitalslivewallpaper.orbitals.touch

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import org.beatonma.orbitals.engine.Region
import org.beatonma.orbitals.physics.GreatAttractor
import org.beatonma.orbitals.physics.Motion
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitals.physics.kg
import org.beatonma.orbitals.physics.uniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine

internal fun Offset.toPosition() = Position(x, y)
internal fun MotionEvent.toPosition() = Position(x, y)
internal fun MotionEvent.PointerCoords.toPosition() = Position(x, y)

internal fun createAttractor(offset: Offset) =
    createAttractor(offset.toPosition())

internal fun createAttractor(event: MotionEvent) =
    createAttractor(event.toPosition())

internal fun createAttractor(coords: MotionEvent.PointerCoords) =
    createAttractor(coords.toPosition())


private fun createAttractor(position: Position) =
    GreatAttractor(
        id = uniqueID("touch"),
        mass = 10_000.kg,
        motion = Motion(position),
    )

internal fun getTouchRegion(event: MotionEvent) = getTouchRegion(event.toPosition())
internal fun getTouchRegion(offset: Offset) = getTouchRegion(offset.toPosition())

private fun getTouchRegion(position: Position): Region {
    val x = position.x.value.toInt()
    val y = position.y.value.toInt()

    return Region(
        x - 250,
        y - 250,
        x + 250,
        y + 250
    )
}

internal fun <Key, Canvas> clearTouchBodies(
    orbitals: OrbitalsRenderEngine<Canvas>,
    bodyMap: MutableMap<Key, UniqueID>
) {
    println("clearTouchBodies")
    val bodies = bodyMap.values.toList()
    bodyMap.clear()
    bodies.forEach { orbitals.removeBody(it) }
}
