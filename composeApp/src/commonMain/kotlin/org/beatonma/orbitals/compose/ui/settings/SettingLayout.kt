package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@Composable
internal fun SettingLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
internal fun CheckableSettingLayout(
    name: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: @Composable () -> Unit
) {
    Row(
        modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)

        content()
    }
}

@Composable
internal fun OutlinedSettingLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SettingLayout(
        modifier
            .border(2.dp, colorScheme.onBackground.copy(alpha = .2f), shapes.medium)
            .clip(shapes.medium)
            .padding(8.dp),
        content = content,
    )
}
