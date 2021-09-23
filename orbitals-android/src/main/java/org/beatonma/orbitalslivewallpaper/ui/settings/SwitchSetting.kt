package org.beatonma.orbitalslivewallpaper.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
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
    SettingLayout {
        CheckableLayout(
            name,
            modifier.fillMaxWidth(),
            onClick = { onValueChange(key, !value) }
        ) {
            Switch(checked = value, onCheckedChange = { checked -> onValueChange(key, checked) })
        }
    }
}
