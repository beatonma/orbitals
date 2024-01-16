package org.beatonma.orbitals.core

import kotlin.math.pow
import kotlin.random.Random


fun squareOf(value: Float): Float = value.pow(2.0F)

val Int.factorial: Int get() = (1..this).reduce { acc, i -> acc * i }


/**
 * Return a list of [Float] of length [divisor] which sum to the receiver value but are distributed
 * unevenly.
 */
internal fun Float.divideUnevenly(divisor: Int, variance: Float = .5f): List<Float> {
    var remaining = this
    val results = mutableListOf<Float>()

    for (n in (divisor) downTo 2) {
        val even = remaining / n
        val uneven = even + (Random.nextFloat() * variance * even * randomDirection)
        remaining -= uneven
        results += uneven
    }
    results += remaining

    return results
}

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
