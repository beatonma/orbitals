package org.beatonma.orbitals.core.util

actual fun debug(content: Any?) {
    console.log("$content")
}

actual fun info(content: Any?) {
    console.info("$content")
}

actual fun warn(content: Any?) {
    console.warn("$content")
}
