package org.beatonma.orbitalslivewallpaper.services

import android.os.Build
import android.service.dreams.DreamService
import android.view.View
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import org.beatonma.orbitalslivewallpaper.R

class ScreensaverService : DreamService(), ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        window.decorView.setTag(R.id.view_tree_view_model_store_owner, this)
        isInteractive = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        setContentView(R.layout.orbitals_view)
    }

    override fun onDestroy() {
        viewModelStore.clear()
        super.onDestroy()
    }
}
