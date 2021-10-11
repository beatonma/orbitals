package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.render.color.MaterialColors
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.IntKey


@Composable
fun ColorSetting(
    name: String,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()

    SettingLayout(modifier) {
        Text(name)
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            state = state,
        ) {
            items(MaterialColors) { color ->
                val c: Color = color.toComposeColor()
                val contentColor = if (c.luminance() > 0.5f) Color.Black else Color.White

                Surface(
                    Modifier
                        .padding(8.dp)
                        .clickable { onValueChange(key, color) }
                        .size(48.dp),
                    color = c,
                    contentColor = contentColor,
                    shape = shapes.small,
                    border = BorderStroke(1.dp, colors.onBackground.copy(alpha = .4f))
                ) {
                    if (value == color) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected color",
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val selectedIndex = MaterialColors.find { it == value } ?: return@LaunchedEffect

        state.animateScrollToItem(selectedIndex)
    }
}
