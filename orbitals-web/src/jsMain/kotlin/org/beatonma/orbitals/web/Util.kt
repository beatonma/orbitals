internal fun Int.toHexString(): String {
    val red = (this shr 16) and 0xff
    val green = (this shr 8) and 0xff
    val blue = this and 0xff

    val rgb = listOf(red, green, blue)
        .map { it.toString(16).padStart(2, padChar = '0') }
        .joinToString("")
    return "#$rgb"
}
