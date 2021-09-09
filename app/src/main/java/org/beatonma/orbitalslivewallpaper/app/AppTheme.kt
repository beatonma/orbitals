package org.beatonma.orbitalslivewallpaper.app

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun AppTheme(
    isDark: Boolean = isSystemInDarkTheme() || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (isDark) darkColors() else lightColors(),
        shapes = Shapes(
            small = CutCornerShape(4.dp),
            medium = CutCornerShape(8.dp),
            large = CutCornerShape(8.dp),
        )
    ) {
        ProvideWindowInsets {
            Surface(
                Modifier.fillMaxSize(),
                content = content
            )
        }
    }
}


private fun lightColors() = androidx.compose.material.lightColors()
private fun darkColors() = androidx.compose.material.darkColors()
