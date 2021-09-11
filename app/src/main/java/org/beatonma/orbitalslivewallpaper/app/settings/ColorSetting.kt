package org.beatonma.orbitalslivewallpaper.app.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.Preferences
import org.beatonma.orbitalslivewallpaper.app.Todo

@Composable
fun ColorSetting(
    name: String,
    key: Preferences.Key<Int>,
    value: Int,
    onValueChange: (key: Preferences.Key<Int>, newValue: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Todo(name)
}
