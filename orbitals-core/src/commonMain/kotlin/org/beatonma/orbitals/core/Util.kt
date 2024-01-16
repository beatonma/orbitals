package org.beatonma.orbitals.core

import kotlin.random.Random

fun chance(likelihood: Float): Boolean = Random.nextFloat() < likelihood
val randomDirection: Float get() = if (Random.nextBoolean()) 1f else -1f
fun randomDirection(magnitude: Float): Float = magnitude * randomDirection
fun <T> randomChoice(vararg options: T): T = options[Random.nextInt(options.size)]
fun Random.nextFloat(min: Float, max: Float) = Random.nextFloat().mapTo(min, max)

val Int.percent get() = this.toFloat().percent
val Float.percent get() = this / 100f


inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}

inline fun <T> List<T>.fastForEachIndexed(action: (Int, T) -> Unit) {
    for (index in indices) {
        val item = get(index)
        action(index, item)
    }
}

internal fun Float.format(decimalPlaces: Int = 2): String = this.toString().run {
    indexOf('.').let { index ->
        when (index) {
            -1 -> this
            else -> take(index + decimalPlaces)
        }
    }
}
