package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun SettingLayout(
    modifier: Modifier = Modifier,
    helpText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    TooltipLayout(helpText) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
internal fun CheckableSettingLayout(
    name: String,
    helpText: String? = null,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    onClick: (() -> Unit),
    role: Role,
    content: @Composable () -> Unit,
) {
    TooltipLayout(helpText) {
        Row(
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick, role = role)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, style = style)

            content()
        }
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


@Composable
internal fun TooltipLayout(text: String?, content: @Composable () -> Unit) {
    text?.let {
        TooltipBox(
            rememberPlainTooltipPositionProvider(0.dp),
            { Tooltip(text) },
            rememberTooltipState(isPersistent = true),
            content = content,
        )
    } ?: content()
}


@Composable
private fun Tooltip(text: String) {
    Text(
        text,
        Modifier
            .background(colorScheme.surfaceVariant, shapes.small)
            .padding(8.dp, 4.dp)
    )
}


@Composable
internal fun maybeStringResource(resource: StringResource?): String? =
    resource?.let { stringResource(it) }
