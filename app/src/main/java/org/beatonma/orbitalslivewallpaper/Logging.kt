@file:Suppress("NOTHING_TO_INLINE")
package org.beatonma.orbitalslivewallpaper

import android.util.Log

inline val Any?.autotag: String
    get() = this?.javaClass?.simpleName ?: "Null"


inline fun <T> T?.warn(content: Any?, tag: String = autotag) =
    Log.w(tag, "$content")

inline fun <T> T?.debug(content: Any?, tag: String = autotag) =
    Log.d(tag, "$content")

inline fun <T> T?.info(content: Any?, tag: String = autotag) =
    Log.i(tag, "$content")

inline fun <T> T?.err(content: Any?, tag: String = autotag) =
    Log.i(tag, "$content")


inline fun warn(content: Any?, tag: String = "Warning") =
    Log.w(tag, "$content")

inline fun debug(content: Any?, tag: String = "Debug") =
    Log.d(tag, "$content")

inline fun info(content: Any?, tag: String = "Info") =
    Log.i(tag, "$content")

inline fun err(content: Any?, tag: String = "Error") =
    Log.i(tag, "$content")
