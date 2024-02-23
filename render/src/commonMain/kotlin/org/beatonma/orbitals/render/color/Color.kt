package org.beatonma.orbitals.render.color

import org.beatonma.orbitals.core.physics.degrees
import kotlin.jvm.JvmInline
import kotlin.math.min

@JvmInline
value class Color(val value: ULong) {
    val alpha: Int get() = (value shr 24).toInt() and 0xff
    val red: Int get() = (value shr 16).toInt() and 0xff
    val green: Int get() = (value shr 8).toInt() and 0xff
    val blue: Int get() = value.toInt() and 0xff

    fun withOpacity(opacity: Float): Color {
        val (r, g, b) = rgb()
        return argb(
            opacity.toUByte(),
            r.toUByte(),
            g.toUByte(),
            b.toUByte()
        )
    }

    fun toRgbInt(): Int = (red shl 16) or (green shl 8) or blue

    fun rgb() = arrayOf(red, green, blue)
    fun argb() = arrayOf(alpha, red, green, blue)
    fun rgba() = arrayOf(red, green, blue, alpha)

    fun hsl(): FloatArray {
        val r = red.normalised()
        val g = green.normalised()
        val b = blue.normalised()

        val rgbMax = maxOf(r, g, b)
        val rgbMin = minOf(r, g, b)

        val chroma: Float = rgbMax - rgbMin
        val lightness: Float = (rgbMax + rgbMin) / 2f

        val hue = if (chroma == 0f) {
            0f
        } else {
            (when (rgbMax) {
                r -> ((g - b) / chroma) % 6f
                g -> ((b - r) / chroma) + 2
                b -> ((r - g) / chroma) + 4
                else -> error("rgbMax $rgbMax is not any of rgb components ($r, $g, $b)")
            } * 60).degrees.asDegrees
        }
        val saturation = when (lightness) {
            0f, 1f -> 0f
            else -> (rgbMax - lightness) / minOf(lightness, 1f - lightness)
        }

        return floatArrayOf(hue, saturation, lightness)
    }

    fun hsla(): FloatArray = floatArrayOf(*hsl(), alpha.normalised())

    fun toStringRgb(): String = rgb().joinToString("") { it.toString(16).padStart(2, '0') }
    fun toStringArgb(): String = argb().joinToString("") { it.toString(16).padStart(2, '0') }

    override fun toString(): String =
        "Color(argb#${argb().joinToString("") { it.toString(16).padStart(2, '0') }} [$value])"

    companion object {
        fun argb(alpha: UByte, red: UByte, green: UByte, blue: UByte): Color =
            Color(
                ((alpha.toLong() shl 24) or
                        (red.toLong() shl 16) or
                        (green.toLong() shl 8) or
                        (blue.toLong())).toULong()
            )

        /**
         * Conversion algorithm: https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB_alternative
         */
        fun hsla(
            hue: Float,
            saturation: Float,
            lightness: Float,
            alpha: Float = 1f
        ): Color {
            require(hue in 0f..360f) { "hue component must be in range 0f..360f (got $hue)" }
            require(saturation in 0f..1f) { "saturation component must be in range 0f..1f (got $saturation)" }
            require(lightness in 0f..1f) { "lightness component must be in range 0f..1f (got $lightness)" }
            require(alpha in 0f..1f) { "alpha component must be in range 0f..1f (got $alpha)" }

            if (saturation == 0f) {
                val lightnessValue = lightness.toUByte()
                return argb(alpha.toUByte(), lightnessValue, lightnessValue, lightnessValue)
            }

            fun f(n: Float): Float {
                val k: Float = (n + hue / 30f) % 12f
                val a: Float = saturation * min(lightness, 1f - lightness)

                return lightness - a * maxOf(-1f, minOf(k - 3f, 9f - k, 1f))
            }

            return argb(alpha.toUByte(), f(0f).toUByte(), f(8f).toUByte(), f(4f).toUByte())
        }

        val Black = Color(0xff000000UL)
        val White = Color(0xffffffffUL)
        val Red = Color(0xffff0000UL)
        val Green = Color(0xff00ff00UL)
        val Blue = Color(0xff0000ffUL)
    }
}

/**
 * Create an opaque Color instance from the given encoded integer.
 *
 * Any alpha component will be ignored - use Color(Long) instead.
 */
fun Color(value: Int): Color {
    require(value < 0x01000000) {
        "When defining a color with alpha, use Color(Long) instead."
    }
    return Color(value.toULong() or 0xff000000UL)
}

fun Int.toColor(): Color = Color(this)
fun ULong.toColor(): Color = Color(this)
fun Color(value: Long): Color = Color(value.toULong())
fun Long.toColor(): Color = Color(toULong())

/**
 * Convert 0f..1f -> 0..255
 */
internal fun Float.toUByte(): UByte = (this * 255f).toInt().toUByte()

/**
 * Convert 0..255 -> 0f..1f.
 */
internal fun Int.normalised(): Float = this.toFloat() / 255f

/**
 * String may be formatted as
 * - a 3-character RGB hex string (with or without '#')
 * - A 6-character RGB hex string (with or without '#')
 * - An 8-character ARGB hex string (with or without '#')
 * - ULong.toString()
 */
fun String.toColor(): Color? {
    val hex = this.removePrefix("#")

    return try {
        when (hex.length) {
            3 -> {
                val (r, g, b) = hex.map {
                    "$it$it".toUByte(16)
                }
                Color.argb(255u, r, g, b)
            }

            6 -> {
                val (r, g, b) = arrayOf(0, 2, 4).map { index ->
                    "${hex[index]}${hex[index + 1]}".toUByte(16)
                }
                Color.argb(255u, r, g, b)
            }

            8 -> {
                val (a, r, g, b) = arrayOf(0, 2, 4, 6).map { index ->
                    "${hex[index]}${hex[index + 1]}".toUByte(16)
                }
                Color.argb(a, r, g, b)
            }

            else -> this.toULong().toColor()
        }
    } catch (e: NumberFormatException) {
        null
    }
}
