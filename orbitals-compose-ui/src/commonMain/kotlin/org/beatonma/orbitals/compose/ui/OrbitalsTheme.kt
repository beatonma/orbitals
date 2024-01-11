package org.beatonma.orbitals.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.core.util.info
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.ObjectColors
import kotlin.random.Random

val ColorScheme.settingsScrim @Composable get() = surface.copy(alpha = .7f)

@Composable
fun OrbitalsTheme(
    options: ColorOptions,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val uiColors by rememberColors(isDark, options.bodies)

    MaterialTheme(
        colorScheme = when {
            isDark -> darkColors(uiColors)
            else -> lightColors(uiColors)
        },
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp),
        )
    ) {
        Surface(
            Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
private fun lightColors(
    colors: UiColors
): ColorScheme {
    return lightColorScheme(
        primary = colors.primary.toComposeColor(),
        onPrimary = colors.onPrimary.toComposeColor(),
        primaryContainer = colors.primaryContainer.toComposeColor(),
        onPrimaryContainer = colors.onPrimaryContainer.toComposeColor(),
        inversePrimary = colors.inversePrimary.toComposeColor(),
        secondary = colors.secondary.toComposeColor(),
        onSecondary = colors.onSecondary.toComposeColor(),
        secondaryContainer = colors.secondaryContainer.toComposeColor(),
        onSecondaryContainer = colors.onSecondaryContainer.toComposeColor(),
        tertiary = colors.tertiary.toComposeColor(),
        onTertiary = colors.onTertiary.toComposeColor(),
        tertiaryContainer = colors.tertiaryContainer.toComposeColor(),
        onTertiaryContainer = colors.onTertiaryContainer.toComposeColor(),
    )
}

@Composable
private fun darkColors(
    colors: UiColors
): ColorScheme {
    info(colors)
    return darkColorScheme(
        primary = colors.primary.toComposeColor(),
        onPrimary = colors.onPrimary.toComposeColor(),
        primaryContainer = colors.primaryContainer.toComposeColor(),
        onPrimaryContainer = colors.onPrimaryContainer.toComposeColor(),
        inversePrimary = colors.inversePrimary.toComposeColor(),
        secondary = colors.secondary.toComposeColor(),
        onSecondary = colors.onSecondary.toComposeColor(),
        secondaryContainer = colors.secondaryContainer.toComposeColor(),
        onSecondaryContainer = colors.onSecondaryContainer.toComposeColor(),
        tertiary = colors.tertiary.toComposeColor(),
        onTertiary = colors.onTertiary.toComposeColor(),
        tertiaryContainer = colors.tertiaryContainer.toComposeColor(),
        onTertiaryContainer = colors.onTertiaryContainer.toComposeColor(),
    )
}

@Composable
private fun rememberColors(
    isDark: Boolean,
    objectColors: Set<ObjectColors>
): MutableState<UiColors> =
    remember(isDark, objectColors) { mutableStateOf(UiColors(isDark, objectColors)) }

private data class UiColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val inversePrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
)

private fun UiColors(isDark: Boolean, objectColors: Set<ObjectColors>): UiColors {
    val (primarySet, secondarySet, tertiarySet) = chooseColorSets(objectColors)

    fun Array<Int>.colorAt(index: Int) = Color(
        when {
            isDark -> this[size - index]
            else -> this[index]
        }
    )

    info("primarySet: ${primarySet.joinToString(",")}")
    info("secondarySet: ${secondarySet.joinToString(",")}")
    info("tertiarySet: ${tertiarySet.joinToString(",")}")

    return UiColors(
        primary = primarySet.colorAt(5),
        onPrimary = primarySet.colorAt(9),
        primaryContainer = primarySet.colorAt(1),
        onPrimaryContainer = primarySet.colorAt(9),
        inversePrimary = primarySet.colorAt(1),
        secondary = secondarySet.colorAt(4),
        onSecondary = secondarySet.colorAt(9),
        secondaryContainer = secondarySet.colorAt(1),
        onSecondaryContainer = secondarySet.colorAt(9),
        tertiary = tertiarySet.colorAt(4),
        onTertiary = tertiarySet.colorAt(9),
        tertiaryContainer = tertiarySet.colorAt(1),
        onTertiaryContainer = tertiarySet.colorAt(9),
    )
}

private fun chooseColorSets(colors: Set<ObjectColors>): Triple<Array<Int>, Array<Int>, Array<Int>> {
    when (colors.size) {
        1 -> {
            val c = colors.first().colors()
            return Triple(c, c, c)
        }

        2 -> {
            return Triple(colors.first().colors(), colors.last().colors(), colors.random().colors())
        }

        else -> {
            val sets = colors.toMutableList()
            return Triple(
                sets.removeAt(Random.nextInt(sets.size)).colors(),
                sets.removeAt(Random.nextInt(sets.size)).colors(),
                sets.removeAt(Random.nextInt(sets.size)).colors()
            )
        }
    }
}
