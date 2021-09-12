package org.beatonma.orbitals.physics

import kotlin.math.pow


fun squareOf(value: Float): Float = value.pow(2.0F)

val Int.factorial: Int get() = (1..this).reduce { acc, i -> acc * i }
