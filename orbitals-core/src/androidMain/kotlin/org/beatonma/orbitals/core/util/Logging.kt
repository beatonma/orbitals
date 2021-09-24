package org.beatonma.orbitals.core.util

import android.util.Log

actual fun debug(content: Any?) {
    Log.d("debug", "$content")
}

actual fun info(content: Any?) {
    Log.i("info", "$content")
}

actual fun warn(content: Any?) {
    Log.w("warn", "$content")
}
