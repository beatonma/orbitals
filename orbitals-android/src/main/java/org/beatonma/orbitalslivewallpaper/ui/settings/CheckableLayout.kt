package org.beatonma.orbitalslivewallpaper.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CheckableLayout(
    name: String,
    modifier: Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val modifierWithClick = if (onClick == null) {
        modifier
    } else {
        Modifier
            .clickable(onClick = onClick)
            .then(modifier)
    }

    Row(
        modifierWithClick,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)

        content()
    }
}
