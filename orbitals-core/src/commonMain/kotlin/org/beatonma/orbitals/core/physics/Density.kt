package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline


@JvmInline
value class Density(override val value: Float) : Scalar {
    override fun toString(): String = "${value}kg/m³"
}
