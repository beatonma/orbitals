package org.beatonma.orbitals.compose.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


data class ButtonData(
    val icon: ImageVector,
    val text: String?,
    val iconContentDescription: String?,
    val onClick: () -> Unit,
)


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
    onClick: () -> Unit,
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
fun Button(data: ButtonData, modifier: Modifier = Modifier) {
    Button(data.icon, data.text, data.iconContentDescription, modifier, data.onClick)
}

@Composable
fun Button(
    icon: ImageVector,
    text: String?,
    iconContentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (text.isNullOrBlank()) {
        IconButton(onClick, modifier) {
            Icon(icon, iconContentDescription)
        }
    } else {
        TextButton(
            onClick,
            modifier,
            colors = buttonColors(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current
            )
        ) {
            ButtonContent(icon, iconContentDescription, text)
        }
    }
}

/**
 * A button with collapsed and expanded states so its visual importance can change depending on context.
 */
@Composable
fun HintButton(
    showText: Boolean,
    icon: ImageVector,
    text: String,
    iconContentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .minimumInteractiveComponentSize()
            .height(HintButtonMinSize)
            .widthIn(min = HintButtonMinSize)
            .clip(CircleShape)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            )
            .padding(horizontal = (HintButtonMinSize - ButtonIconSize) / 2),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonIcon(icon, iconContentDescription)

        AnimatedVisibility(
            showText,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            Row {
                Spacer(Modifier.width(ButtonContentSpacing))
                Text(text)
            }
        }
    }
}

@Composable
fun HintButton(
    icon: ImageVector,
    text: String,
    iconContentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var showText by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(HintDuration)
        showText = false
    }

    HintButton(showText, icon, text, iconContentDescription, modifier, onClick)
}


@Composable
private fun ButtonContent(icon: ImageVector?, iconContentDescription: String?, text: String?) {
    SpacedRow(spacing = ButtonContentSpacing, verticalAlignment = Alignment.CenterVertically) {
        icon?.let { ButtonIcon(icon, iconContentDescription) }
        text?.let { Text(text) }
    }
}

@Composable
private fun ButtonIcon(icon: ImageVector, iconContentDescription: String?) {
    Icon(icon, iconContentDescription, Modifier.size(ButtonIconSize))
}

private const val HintDuration = 1500L
private val ButtonIconSize = 18.dp
private val ButtonContentSpacing = 8.dp
private val HintButtonMinSize = 40.dp
