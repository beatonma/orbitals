package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal val SettingModifier = Modifier
    .padding(16.dp)
    .fillMaxWidth()

@Composable
internal fun SettingLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = SettingModifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}
