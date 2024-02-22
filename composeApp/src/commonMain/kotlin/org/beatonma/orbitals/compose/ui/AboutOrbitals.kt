@file:OptIn(ExperimentalResourceApi::class)

package org.beatonma.orbitals.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import orbitals.composeapp.generated.resources.Res
import org.beatonma.orbitals.compose.ui.components.SpacedColumn
import org.beatonma.orbitals.compose.ui.components.SpacedRow
import org.beatonma.orbitals.compose.ui.components.TextBlock
import org.beatonma.orbitals.compose.ui.components.TonalButton
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
private fun InstanceLink(modifier: Modifier = Modifier) {
    if (platform.isWeb) return

    val uriHandler = LocalUriHandler.current
    val url = stringResource(Res.string.about__orbitals__webapp_url)

    TonalButton(
        text = stringResource(Res.string.about__orbitals__webapp),
        icon = OrbitalsIcons.Mb,
        iconContentDescription = null,
        onClick = { uriHandler.openUri(url) },
        modifier = modifier,
    )
}

@Composable
private fun GithubLink(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val url = stringResource(Res.string.about__orbitals__github_url)

    TonalButton(
        text = stringResource(Res.string.about__orbitals__github),
        icon = OrbitalsIcons.Github,
        iconContentDescription = null,
        onClick = { uriHandler.openUri(url) },
        modifier = modifier,
    )
}
