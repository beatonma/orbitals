package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.orbitals.core.map
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import kotlin.math.roundToInt


@Composable
fun IntegerSetting(
    name: String,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        value = value,
        onOffsetChange = { offset ->
            onValueChange(key, offset.roundToInt())
        },
        min = min,
        max = max,
        modifier = modifier,
    )
}


@Composable
fun FloatSetting(
    name: String,
    key: FloatKey,
    value: Float,
    onValueChange: (key: FloatKey, newValue: Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        value = value,
        onOffsetChange = { offset ->
            onValueChange(key, offset)
        },
        min = min,
        max = max,
        modifier = modifier,
    )
}


@Composable
private fun <N : Number> NumberSettingLayout(
    name: String,
    value: N,
    onOffsetChange: (Float) -> Unit,
    min: N,
    max: N,
    modifier: Modifier,
) {
    SettingLayout {
        BoxWithConstraints {
            val maxWidth = constraints.maxWidth.toFloat()
            var offset: Float by remember {
                mutableStateOf(value.toFloat().map(min.toFloat(), max.toFloat(), 0f, maxWidth))
            }

            Column(
                modifier.draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offset = (offset + delta).coerceIn(0f, maxWidth)
                        onOffsetChange(offset.map(0f, maxWidth, min.toFloat(), max.toFloat()))
                    }
                )
            ) {
                Text("$name: $value", Modifier.align(Alignment.CenterHorizontally))

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("$min")
                    LinearProgressIndicator(
                        progress = value.toFloat().map(min.toFloat(), max.toFloat(), 0f, 1f),
                    )
                    Text("$max")
                }
            }
        }
    }
}
