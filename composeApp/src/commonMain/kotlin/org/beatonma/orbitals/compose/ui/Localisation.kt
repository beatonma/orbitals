package org.beatonma.orbitals.compose.ui

import androidx.compose.runtime.Composable
import orbitals.composeapp.generated.resources.Res
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.RenderLayer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass


@OptIn(ExperimentalResourceApi::class)
internal object Localisation {
    /**
     * Retrieve a mapping of Enum values to translatable string resources.
     */
    @Suppress("UNCHECKED_CAST")
    val <E : Enum<E>> KClass<out E>.stringResourceMap: Map<E, StringResource>
        get() = when (this) {
            DrawStyle::class -> DrawStyleLocalisation
            ObjectColors::class -> ColorsLocalisation
            RenderLayer::class -> RenderLayerLocalisation
            SystemGenerator::class -> GeneratorLocalisation
            CollisionStyle::class -> CollisionStyleLocalisation
            else -> throw IllegalArgumentException("Unhandled class $this")
        } as Map<E, StringResource>

    /**
     * Accessor for [stringResourceMap].
     */
    @Composable
    fun <E : Enum<E>> Map<E, StringResource>.getString(key: E): String =
        this[key]?.let { stringResource(it) } ?: "_badstr_: $key"

    private val DrawStyleLocalisation: Map<DrawStyle, StringResource> = mapOf(
        DrawStyle.Solid to Res.string.settings__visual__enum_drawstyle__solid,
        DrawStyle.Wireframe to Res.string.settings__visual__enum_drawstyle__wireframe
    )

    private val ColorsLocalisation: Map<ObjectColors, StringResource> = mapOf(
        ObjectColors.Greyscale to Res.string.settings__color__enum_objectcolors__grey,
        ObjectColors.Red to Res.string.settings__color__enum_objectcolors__red,
        ObjectColors.Orange to Res.string.settings__color__enum_objectcolors__orange,
        ObjectColors.Yellow to Res.string.settings__color__enum_objectcolors__yellow,
        ObjectColors.Green to Res.string.settings__color__enum_objectcolors__green,
        ObjectColors.Blue to Res.string.settings__color__enum_objectcolors__blue,
        ObjectColors.Purple to Res.string.settings__color__enum_objectcolors__purple,
        ObjectColors.Pink to Res.string.settings__color__enum_objectcolors__pink,
        ObjectColors.Any to Res.string.settings__color__enum_objectcolors__any,
    )

    private val RenderLayerLocalisation: Map<RenderLayer, StringResource> = mapOf(
        RenderLayer.Default to Res.string.settings__visual__enum_renderlayer__default,
        RenderLayer.Acceleration to Res.string.settings__visual__enum_renderlayer__acceleration,
        RenderLayer.Trails to Res.string.settings__visual__enum_renderlayer__trails
    )

    private val GeneratorLocalisation: Map<SystemGenerator, StringResource> = mapOf(
        SystemGenerator.StarSystem to Res.string.settings__physics__enum_systemgenerator__starsystem,
        SystemGenerator.Randomized to Res.string.settings__physics__enum_systemgenerator__randomized,
        SystemGenerator.Asteroids to Res.string.settings__physics__enum_systemgenerator__asteroids,
        SystemGenerator.Gauntlet to Res.string.settings__physics__enum_systemgenerator__gauntlet,
        SystemGenerator.GreatAttractor to Res.string.settings__physics__enum_systemgenerator__greatattractor,
    )

    private val CollisionStyleLocalisation: Map<CollisionStyle, StringResource> = mapOf(
        CollisionStyle.None to Res.string.settings__physics__enum_collisionstyle__none,
        CollisionStyle.Merge to Res.string.settings__physics__enum_collisionstyle__merge,
        CollisionStyle.Break to Res.string.settings__physics__enum_collisionstyle__explode,
        CollisionStyle.Bouncy to Res.string.settings__physics__enum_collisionstyle__bouncy,
        CollisionStyle.Sticky to Res.string.settings__physics__enum_collisionstyle__sticky,
    )
}
