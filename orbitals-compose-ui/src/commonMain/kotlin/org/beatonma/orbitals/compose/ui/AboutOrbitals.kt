package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.compose.ui.components.SpacedColumn
import org.beatonma.orbitals.compose.ui.components.SpacedRow
import org.beatonma.orbitals.compose.ui.components.icons.OrbitalsIcons
import org.beatonma.orbitals.compose.ui.components.icons.orbitals.Github
import org.beatonma.orbitals.compose.ui.components.icons.orbitals.Mb
import org.beatonma.orbitals.core.platform


@Composable
fun AboutOrbitals(modifier: Modifier = Modifier) {
    SpacedColumn(modifier) {
        Text(
            """Welcome to Orbitals: an N-body gravity playground.
                |
                |This multiplatform project is open source and written with Kotlin and Compose.
        """.trimMargin()
        )

        SpacedRow(Modifier.align(Alignment.End)) {
            InstanceLink()
            GithubLink()
        }
    }
}

@Composable
private fun LinkButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = {
            Icon(
                icon,
                "",
                Modifier.size(24.dp)
            )
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun InstanceLink(modifier: Modifier = Modifier) {
    if (platform.isWeb) return

    val uriHandler = LocalUriHandler.current
    LinkButton(
        text = "Web app",
        icon = OrbitalsIcons.Mb,
        onClick = { uriHandler.openUri("https://beatonma.org/webapp/orbitals/") },
        modifier = modifier,
    )
}

@Composable
private fun GithubLink(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    LinkButton(
        text = "Github",
        icon = OrbitalsIcons.Github,
        onClick = { uriHandler.openUri("https://github.com/beatonma/orbitals/") },
        modifier = modifier,
    )
}
