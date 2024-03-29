package org.beatonma.orbitals.core.engine

import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.metres
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

interface Space {
    val start: Int
    val top: Int
    val end: Int
    val bottom: Int

    val width: Int get() = end - start
    val height: Int get() = bottom - top

    val area: Int get() = width * height
    val isValid: Boolean get() = width > 1f && height > 1f

    val center: Position
        get() = Position(
            (start + width / 2f).metres,
            (top + height / 2f).metres
        )

    /**
     * Largest radius of a circle that can fit inside this space.
     */
    val radius: Int get() = min(width, height) / 2

    fun contains(x: Int, y: Int): Boolean {
        return when {
            x < start -> false
            x > end -> false
            y < top -> false
            y > bottom -> return false
            else -> return true
        }
    }
}

data class Universe internal constructor(
    override val start: Int,
    override val top: Int,
    override val end: Int,
    override val bottom: Int,
    val visibleSpace: Region,
) : Space {
    override fun toString(): String {
        return "Universe($start, $top, $end, $bottom)"
    }
}

data class Region(
    override val start: Int,
    override val top: Int,
    override val end: Int,
    override val bottom: Int,
) : Space {
    override fun toString(): String {
        return "Region($start, $top, $end, $bottom)"
    }
}

/**
 * Return a [Universe] centered on the region (0, 0, focusWidth, focusHeight) with a surrounding
 * margin of overflow * (width | height)
 */
fun Universe(windowWidth: Int, windowHeight: Int, overflow: Float = Config.UniverseOverflow): Universe {
    val overflowWidth = (overflow * windowWidth).roundToInt()
    val overflowHeight = (overflow * windowHeight).roundToInt()

    return Universe(
        start = -overflowWidth,
        top = -overflowHeight,
        end = windowWidth + overflowWidth,
        bottom = windowHeight + overflowHeight,
        visibleSpace = Region(0, 0, windowWidth, windowHeight)
    )
}


fun Space.relativePosition(
    x: Float,
    y: Float,
): Position = Position(
    (start + width * x).metres,
    (top + height * y).metres
)

fun Universe.relativeVisiblePosition(
    x: Float,
    y: Float,
): Position {
    check(x in 0f..1f && y in 0f..1f)
    return visibleSpace.relativePosition(x, y)
}

fun Universe.anyVisiblePosition(): Position =
    relativeVisiblePosition(
        Random.nextFloat(),
        Random.nextFloat(),
    )
