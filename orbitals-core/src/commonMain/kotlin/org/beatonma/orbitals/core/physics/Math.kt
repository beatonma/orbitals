package org.beatonma.orbitals.core.physics

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

val randomDirection: Float get() = if (Random.nextBoolean()) 1f else -1f
fun randomDirection(magnitude: Float): Float = magnitude * randomDirection
fun <T> randomChoice(vararg options: T): T = options[Random.nextInt(options.size)]
