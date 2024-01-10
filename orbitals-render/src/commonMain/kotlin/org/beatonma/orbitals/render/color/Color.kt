package org.beatonma.orbitals.render.color

import kotlin.jvm.JvmInline

@JvmInline
value class Color(val value: ULong) {
    val alpha: Int get() = (value shr 24).toInt() and 0xff
    val red: Int get() = (value shr 16).toInt() and 0xff
    val green: Int get() = (value shr 8).toInt() and 0xff
    val blue: Int get() = value.toInt() and 0xff

    fun withOpacity(opacity: Float): Color {
        val (_, r, g, b) = argb()
        return fromArgb(
            (opacity * 0xff.toFloat()).toInt().toUByte(),
            r.toUByte(),
            g.toUByte(),
            b.toUByte()
        )
    }

    fun rgb() = arrayOf(red, green, blue)

    fun argb() = arrayOf(alpha, red, green, blue)

    fun rgba() = arrayOf(red, green, blue, alpha)

    fun toRgbInt(): Int = (red shl 16) or (green shl 8) or blue

    override fun toString(): String =
        "Color(${argb().joinToString("") { it.toString(16).padStart(2, '0') }} [$value])"

    companion object {
        internal fun fromArgb(alpha: UByte, red: UByte, green: UByte, blue: UByte): Color {
            return Color(
                ((alpha.toLong() shl 24) or
                        (red.toLong() shl 16) or
                        (green.toLong() shl 8) or
                        (blue.toLong())).toULong()
            )
        }
    }
}

/**
 * Create an opaque Color instance from the given encoded integer.
 *
 * Any alpha component will be ignored - use Color(Long) instead.
 */
fun Color(value: Int): Color = Color(value.toULong() or 0xff000000UL)
fun Color(value: Long): Color = Color(value.toULong())
