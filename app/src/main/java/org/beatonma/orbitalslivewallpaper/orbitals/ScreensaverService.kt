package org.beatonma.orbitalslivewallpaper.orbitals

import android.os.Build
import android.service.dreams.DreamService
import android.view.View
import com.beatonma.orbitalslivewallpaper.R

class ScreensaverService: DreamService() {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isInteractive = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        setContentView(R.layout.orbitals_view)
    }
}
