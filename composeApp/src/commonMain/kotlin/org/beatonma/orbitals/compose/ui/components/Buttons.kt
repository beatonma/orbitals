package org.beatonma.orbitals.compose.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


data class ButtonData(
    val icon: ImageVector,
    val text: String?,
    val iconContentDescription: String?,
    val onClick: () -> Unit,
)

@Composable
fun Fab(data: ButtonData) {
    Fab(data.icon, data.text, data.iconContentDescription, data.onClick)
}

@Composable
fun Fab(icon: ImageVector, text: String?, iconContentDescription: String?, onClick: () -> Unit) {
    if (text.isNullOrBlank()) {
        FloatingActionButton(
            content = { Icon(icon, iconContentDescription) },
            onClick = onClick,
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface,
        )
    } else {
        ExtendedFloatingActionButton(
            text = { Text(text) },
            icon = { Icon(icon, iconContentDescription) },
            onClick = onClick,
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface,
        )
    }
}


@Composable
fun TonalButton(data: ButtonData, modifier: Modifier = Modifier) {
    TonalButton(data.icon, data.text, data.iconContentDescription, modifier, data.onClick)
}

@Composable
fun TonalButton(
    icon: ImageVector,
    text: String?,
    iconContentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (text.isNullOrBlank()) {
        FilledTonalIconButton(onClick, modifier) {
            Icon(icon, iconContentDescription)
        }
    } else {
        FilledTonalButton(onClick, modifier) {
            ButtonContent(icon, iconContentDescription, text)
        }
    }
}


@Composable
private fun ButtonContent(icon: ImageVector?, iconContentDescription: String?, text: String?) {
    SpacedRow(spacing = 8.dp, verticalAlignment = Alignment.CenterVertically) {
        icon?.let { Icon(icon, iconContentDescription, Modifier.size(18.dp)) }
        text?.let { Text(text) }
    }
}
