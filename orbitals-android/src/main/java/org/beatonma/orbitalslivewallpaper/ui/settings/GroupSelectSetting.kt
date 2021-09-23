package org.beatonma.orbitalslivewallpaper.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.datastore.preferences.core.Preferences

@Composable
fun <E : Enum<E>> SingleSelectSetting(
    name: String,
    key: Preferences.Key<String>,
    value: E,
    values: Array<out E>,
    onValueChange: (key: Preferences.Key<String>, newValue: E) -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsibleGroup(name, modifier) {
        for (v in values) {
            val onClick = { onValueChange(key, v) }
            CheckableLayout(
                v.name,
                SettingModifier,
                onClick,
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
    key: Preferences.Key<Set<String>>,
    value: Set<E>,
    values: Array<out E>,
    onValueChange: (key: Preferences.Key<Set<String>>, newValue: Set<E>) -> Unit,
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
                    onValueChange(key, value + v)
                }
            }

            CheckableLayout(
                v.name,
                SettingModifier,
                onClick,
            ) {
                Checkbox(
                    checked = v in value,
                    onCheckedChange = { onClick() }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
        Modifier
            .border(2.dp, colors.onBackground.copy(alpha = .2f), shapes.medium)
            .clip(shapes.medium)
            .padding(8.dp)
    ) {
        Column(modifier) {
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
