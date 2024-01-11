package org.beatonma.orbitals.compose.ui.settings

import LabelledSlider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        onOffsetChange = { offset -> onValueChange(key, offset.roundToInt()) },
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
        onOffsetChange = { offset -> onValueChange(key, offset) },
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
        Column(
            modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("$name: $value", Modifier.align(Alignment.CenterHorizontally))

            LabelledSlider(
                value = value.toFloat(),
                onValueChange = onOffsetChange,
                min = min.toFloat(),
                max = max.toFloat(),
            )
        }
    }
}
