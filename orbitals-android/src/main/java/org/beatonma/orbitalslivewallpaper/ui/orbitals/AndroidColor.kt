package org.beatonma.orbitalslivewallpaper.ui.orbitals

import org.beatonma.orbitals.render.color.Color


fun Color.toAndroidColor() = toRgbInt() or 0xff000000.toInt()
