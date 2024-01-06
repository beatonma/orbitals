package org.beatonma.orbitals.render.color

import kotlin.jvm.JvmInline

@JvmInline
value class Color(val value: Long) {
    val alpha: Long get() = (value shr 24) and 0xff
    val red: Long get() = (value shr 16) and 0xff
    val green: Long get() = (value shr 8) and 0xff
    val blue: Long get() = value and 0xff

    val alphaInt: Int get() = (value shr 24).toInt() and 0xff
    val redInt: Int get() = (value shr 16).toInt() and 0xff
    val greenInt: Int get() = (value shr 8).toInt() and 0xff
    val blueInt: Int get() = value.toInt() and 0xff

    fun withOpacity(opacity: Float): Color {
        val (_, r, g, b) = argb()
        return fromArgb((opacity * 0xff.toFloat()).toLong(), r, g, b)
    }

    fun rgb() = arrayOf(red, green, blue)

    fun argb() = arrayOf(alpha, red, green, blue)

    fun rgba() = arrayOf(red, green, blue, alpha)

    fun toRgbInt(): Int {
        return (redInt shl 16) or
                (greenInt shl 8) or
                blueInt
    }

    companion object {
        private fun fromArgb(alpha: Long, red: Long, green: Long, blue: Long): Color {
            return Color(
                (alpha shl 24) or
                (red shl 16) or
                (green shl 8) or
                blue
            )
        }
    }
}

fun Color(value: Int): Color = Color(0xff000000 or value.toLong())
