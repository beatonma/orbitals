package org.beatonma.orbitalslivewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.beatonma.orbitalslivewallpaper.ui.App
import org.beatonma.orbitalslivewallpaper.ui.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                App()
            }
        }
    }
}
