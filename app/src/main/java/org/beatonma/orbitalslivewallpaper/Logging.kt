package org.beatonma.orbitalslivewallpaper

import android.util.Log

val Any?.autotag: String
    get() = this?.javaClass?.simpleName ?: "Null"


fun <T> T?.warn(content: Any?, tag: String = autotag) =
    Log.w(tag, "$content")

fun <T> T?.debug(content: Any?, tag: String = autotag) =
    Log.d(tag, "$content")

fun <T> T?.info(content: Any?, tag: String = autotag) =
    Log.i(tag, "$content")

fun <T> T?.err(content: Any?, tag: String = autotag) =
    Log.i(tag, "$content")


fun warn(content: Any?, tag: String = "Warning") =
    Log.w(tag, "$content")

fun debug(content: Any?, tag: String = "Debug") =
    Log.d(tag, "$content")

fun info(content: Any?, tag: String = "Info") =
    Log.i(tag, "$content")

fun err(content: Any?, tag: String = "Error") =
    Log.i(tag, "$content")
