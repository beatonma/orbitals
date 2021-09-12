package org.beatonma.orbitals

import androidx.annotation.FloatRange
import kotlin.random.Random

/**
 * Calculate position relative to toMin..toMax based on relative position in range fromMin..fromMax.
 */
fun Float.map(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float =
    normalizeIn(fromMin, fromMax)
        .mapTo(toMin, toMax)

/**
 * Receiver value is assumed to be between 0F..1F!
 */
fun Float.mapTo(min: Float, max: Float): Float {
    val range: Float = max - min
    return (min + (this * range))
        .coerceIn(min, max)
}

/**
 * Map to a value between 0F..1F relative to the given limits.
 */
fun Float.normalizeIn(min: Float, max: Float): Float {
    val range = max - min
    return ((this - min) / range)
        .coerceIn(0F, 1F)
}

fun chance(@FloatRange(from = 0.0, to = 1.0) likelihood: Float): Boolean =
    Random.nextFloat() < likelihood

val Int.percent get() = this.toFloat().percent
val Float.percent get() = this / 100f
