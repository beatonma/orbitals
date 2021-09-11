package org.beatonma.orbitalslivewallpaper.app.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Column(modifier) {
        Text(name, style = MaterialTheme.typography.h5)

        for (v in values) {
            val onClick = { onValueChange(key, v) }
            CheckableLayout(
                v.name,
                modifier,
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
    allowEmptySet: Boolean = false,
) {
    Column(modifier) {
        Text(name, style = MaterialTheme.typography.h5)

        for (v in values) {
            val onClick = {
                if (v in value) {
                    val newValue = value.filter { it != v }.toSet()
                    if (!allowEmptySet && newValue.isEmpty()) {
                        onValueChange(key, setOf(values.first()))
                    } else {
                        onValueChange(key, newValue)
                    }
                } else {
                    onValueChange(key, value + v)
                }
            }

            CheckableLayout(
                v.name,
                modifier,
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
