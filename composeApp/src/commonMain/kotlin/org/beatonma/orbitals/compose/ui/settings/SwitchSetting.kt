package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beatonma.orbitals.render.options.BooleanKey

@Composable
fun SwitchSetting(
    name: String,
    key: BooleanKey,
    value: Boolean,
    onValueChange: (key: BooleanKey, value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingLayout {
        CheckableSettingLayout(
            name,
            modifier.fillMaxWidth(),
            onClick = { onValueChange(key, !value) }
        ) {
            Switch(checked = value, onCheckedChange = { checked -> onValueChange(key, checked) })
        }
    }
}
