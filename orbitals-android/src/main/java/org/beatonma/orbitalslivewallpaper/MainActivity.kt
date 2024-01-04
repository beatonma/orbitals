package org.beatonma.orbitalslivewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.beatonma.orbitalslivewallpaper.ui.AppTheme
import org.beatonma.orbitalslivewallpaper.ui.SettingsUI


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                SettingsUI()
            }
        }
    }
}
