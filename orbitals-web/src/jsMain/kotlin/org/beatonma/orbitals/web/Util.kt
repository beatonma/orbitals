internal fun Int.toHexString(): String {
    val red = (this shr 16) and 0xff
    val green = (this shr 8) and 0xff
    val blue = this and 0xff

    return "#${red.toString(16)}${green.toString(16)}${blue.toString(16)}"
}
