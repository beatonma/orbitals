package org.beatonma.orbitalslivewallpaper


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.beatonma.orbitalslivewallpaper.ui.AppTheme
import org.beatonma.orbitalslivewallpaper.ui.SettingsUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                SettingsUI()
            }
        }
    }
}
