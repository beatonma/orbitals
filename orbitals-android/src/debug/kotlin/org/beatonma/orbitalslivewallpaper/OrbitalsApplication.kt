package org.beatonma.orbitalslivewallpaper

import android.app.Application
import android.os.StrictMode
import org.beatonma.orbitals.core.util.warn

class DebugApplication : Application() {
    init {
        warn("DEBUG: STRICT MODE ENABLED")
        StrictMode.enableDefaults()
    }
}
