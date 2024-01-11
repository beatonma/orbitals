package org.beatonma.orbitalslivewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.beatonma.orbitalslivewallpaper.ui.AppTheme
import org.beatonma.orbitalslivewallpaper.ui.SettingsUI


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                SettingsUI()
            }
        }
    }
}
