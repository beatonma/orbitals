package org.beatonma.orbitals.compose.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey

@Composable
fun <E : Enum<E>> SingleSelectSetting(
    name: String,
    key: StringKey<E>,
    value: E,
    values: Array<out E>,
    onValueChange: (key: StringKey<E>, newValue: E) -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsibleGroup(name, modifier) {
        for (v in values) {
            val onClick = { onValueChange(key, v) }
            CheckableSettingLayout(
                v.name,
                onClick = onClick,
            ) {
                RadioButton(
                    selected = value == v,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
fun <E : Enum<E>> MultiSelectSetting(
    name: String,
    key: StringSetKey<E>,
    value: Set<E>,
    values: Array<out E>,
    onValueChange: (key: StringSetKey<E>, newValue: Set<E>) -> Unit,
    modifier: Modifier = Modifier,
    defaultValue: E = values.first(),
    allowEmptySet: Boolean = false,
) {
    CollapsibleGroup(name, modifier) {
        for (v in values) {
            val onClick = {
                if (v in value) {
                    val newValue = value.filter { it != v }.toSet()
                    if (!allowEmptySet && newValue.isEmpty()) {
                        onValueChange(key, setOf(defaultValue))
                    } else {
                        onValueChange(key, newValue)
                    }
                } else {
                    onValueChange(key, (value + v).toSet())
                }
            }

            CheckableSettingLayout(
                v.name,
                onClick = onClick,
            ) {
                Checkbox(
                    checked = v in value,
                    onCheckedChange = { onClick() }
                )
            }
        }
    }
}

@Composable
private fun CollapsibleGroup(
    name: String,
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val onClick = { expanded = !expanded }

    SettingLayout(
        modifier
            .border(2.dp, colors.onBackground.copy(alpha = .2f), shapes.medium)
            .clip(shapes.medium)
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(name, style = MaterialTheme.typography.h5)

                IconButton(
                    onClick = onClick,
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        "Show ${if (expanded) "more" else "less"}",
                        modifier = Modifier.rotate(iconRotation)
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(content = content)
            }
        }
    }
}
