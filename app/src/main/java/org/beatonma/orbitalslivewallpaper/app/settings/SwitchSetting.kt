package org.beatonma.orbitalslivewallpaper.app.settings

import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.Preferences

@Composable
fun SwitchSetting(
    name: String,
    key: Preferences.Key<Boolean>,
    value: Boolean,
    onValueChange: (key: Preferences.Key<Boolean>, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckableLayout(
        name,
        modifier,
        onClick = { onValueChange(key, !value) }
    ) {
        Switch(checked = value, onCheckedChange = { checked -> onValueChange(key, checked) })
    }
}
