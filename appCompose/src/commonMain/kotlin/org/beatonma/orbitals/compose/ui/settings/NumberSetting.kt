package org.beatonma.orbitals.compose.ui.settings

import LabelledSlider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import kotlin.math.roundToInt


@Composable
fun IntegerSetting(
    name: String,
    helpText: String?,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
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
    helpText: String?,
    key: FloatKey,
    value: Float,
    onValueChange: (key: FloatKey, newValue: Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
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
    helpText: String?,
    value: N,
    onOffsetChange: (Float) -> Unit,
    min: N,
    max: N,
    modifier: Modifier,
) {
    SettingLayout(helpText = helpText) {
        Column(
            modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("$name: ${value.format()}", Modifier.align(Alignment.CenterHorizontally))

            LabelledSlider(
                value = value.toFloat(),
                onValueChange = onOffsetChange,
                min = min.toFloat(),
                max = max.toFloat(),
            )
        }
    }
}


fun Number.format(decimalPlaces: Int = 2) = this.toString().run {
    indexOf('.').let { index ->
        when (index) {
            -1 -> this
            else -> take(index + decimalPlaces + 1)
        }
    }
}
