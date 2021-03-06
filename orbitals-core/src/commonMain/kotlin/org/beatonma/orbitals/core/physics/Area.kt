package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline
import kotlin.math.sqrt

/**
 * Square metres
 */
@JvmInline
value class Area internal constructor(override val value: Float): Scalar {
    operator fun plus(other: Area) = Area(value + other.value)
}

fun sqrt(area: Area): Distance = Distance(sqrt(area.value))
