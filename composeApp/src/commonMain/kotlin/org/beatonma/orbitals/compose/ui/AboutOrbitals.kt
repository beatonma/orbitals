@file:OptIn(ExperimentalResourceApi::class)
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
import orbitals.composeapp.generated.resources.Res
import org.beatonma.orbitals.compose.ui.components.SpacedColumn
import org.beatonma.orbitals.compose.ui.components.SpacedRow
import org.beatonma.orbitals.compose.ui.components.TextBlock
import org.beatonma.orbitals.compose.ui.components.icons.OrbitalsIcons
import org.beatonma.orbitals.compose.ui.components.icons.orbitals.Github
import org.beatonma.orbitals.compose.ui.components.icons.orbitals.Mb
import org.beatonma.orbitals.core.platform
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource


@Composable
fun AboutOrbitals(modifier: Modifier = Modifier) {
    SpacedColumn(modifier) {
        TextBlock(stringResource(Res.string.about__blurb))

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
    val url = stringResource(Res.string.about__orbitals__webapp_url)

    LinkButton(
        text = stringResource(Res.string.about__orbitals__webapp),
        icon = OrbitalsIcons.Mb,
        onClick = { uriHandler.openUri(url) },
        modifier = modifier,
    )
}

@Composable
private fun GithubLink(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val url = stringResource(Res.string.about__orbitals__github_url)

    LinkButton(
        text = stringResource(Res.string.about__orbitals__github),
        icon = OrbitalsIcons.Github,
        onClick = { uriHandler.openUri(url) },
        modifier = modifier,
    )
}
