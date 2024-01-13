package org.beatonma.orbitals.compose.ui.settings

import LabelledSlider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.beatonma.orbitals.compose.ui.components.DraggableRow
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.color.MaterialColorSwatch
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.IntKey
import kotlin.math.max
import androidx.compose.ui.graphics.Color as ComposeColor


@Composable
fun ColorSetting(
    name: String,
    key: ColorKey,
    value: Color,
    onValueChange: (key: ColorKey, newValue: Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val hsl = rememberHsl(value)
    val colors = MaterialColorSwatch
    val swatchState = rememberLazyListState()

    LaunchedEffect(hsl.hue, hsl.saturation, hsl.lightness) {
        onValueChange(key, Color.hsla(hsl.hue, hsl.saturation, hsl.lightness))
    }

    val swatchIndex = colors.indexOf(value.toRgbInt())
    if (swatchIndex >= 0) {
        scope.launch { swatchState.animateScrollToItem(max(0, swatchIndex - 1)) }
    }

    OutlinedSettingLayout(modifier.heightIn(max = 450.dp)) {
        Text(name)

        DraggableRow(state = swatchState) {
            items(colors) { c ->
                val color = Color(c)
                val composeColor = color.toComposeColor()
                val contentColor = when {
                    composeColor.luminance() > 0.5f -> ComposeColor.Black
                    else -> ComposeColor.White
                }
                Patch(
                    color = composeColor,
                    contentColor = contentColor,
                    isSelected = c == value.toRgbInt(),
                ) {
                    val (h, s, l) = color.hsl()
                    hsl.hue = h
                    hsl.saturation = s
                    hsl.lightness = l
                }
            }
        }

        Column(
            Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HslComponent(
                hsl.hue,
                { hsl.hue = it },
                "H",
                0f,
                360f,
            )
            HslComponent(
                hsl.saturation,
                { hsl.saturation = it },
                "S",
                0f,
                1f,
            )
            HslComponent(
                hsl.lightness,
                { hsl.lightness = it },
                "L",
                0f,
                1f,
            )
        }
    }
}

@Composable
private fun rememberHsl(color: Color): HslColor {
    val (h, s, l) = color.hsl()

    return remember { HslColor(h, s, l) }
}

private class HslColor(h: Float, s: Float, l: Float) {
    var hue by mutableStateOf(h)
    var saturation by mutableStateOf(s)
    var lightness by mutableStateOf(l)
}

@Composable
private fun HslComponent(
    value: Float,
    onValueChange: (Float) -> Unit,
    name: String,
    min: Float,
    max: Float,
) {
    LabelledSlider(
        value = value,
        onValueChange = onValueChange,
        min = min,
        max = max,
        startLabel = name,
    )
}

private val PatchSize = 48.dp
private val PatchSpacing = 8.dp

@Composable
private fun Patch(
    color: ComposeColor,
    contentColor: ComposeColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(PatchSpacing)
            .size(PatchSize),
        color = color,
        contentColor = contentColor,
        shape = shapes.small,
        border = BorderStroke(1.dp, colorScheme.onBackground.copy(alpha = .4f))
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected color",
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
