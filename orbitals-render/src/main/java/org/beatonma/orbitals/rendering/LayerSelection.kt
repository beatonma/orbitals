package org.beatonma.orbitals.rendering

import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.options.RenderLayer
import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.rendering.renderer.AccelerationRenderer
import org.beatonma.orbitals.rendering.renderer.DripRenderer
import org.beatonma.orbitals.rendering.renderer.SimpleRenderer
import org.beatonma.orbitals.rendering.renderer.TrailRenderer
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import android.graphics.Canvas as AndroidCanvas

private typealias LayerSet = Set<RenderLayer>
private typealias RenderSet<Canvas> = Set<OrbitalsRenderer<Canvas>>

private typealias AndroidRenderer = OrbitalsRenderer<AndroidCanvas>
private typealias ComposeRenderer = OrbitalsRenderer<DrawScope>

/**
 * Layers must be registered here renderer classes for AndroidCanvas and DrawScope!
 */
private object LayerRegistry {
    private val registry: Map<RenderLayer, Layer<*>> = mapOf(
        RenderLayer.Default to Layer(SimpleRenderer::class),
        RenderLayer.Trails to Layer(TrailRenderer::class),
        RenderLayer.Acceleration to Layer(AccelerationRenderer::class),
        RenderLayer.Drip to Layer(DripRenderer::class)
    )

    operator fun get(key: RenderLayer): Layer<*> = registry[key]!!

    fun getLayerType(renderer: OrbitalsRenderer<*>): RenderLayer {
        val cls = renderer::class.java

        registry.forEach { (layer, renderers) ->
            val baseClass = renderers.renderClass
            if (baseClass.java.isAssignableFrom(cls)) return layer
        }

        throw IllegalArgumentException("Unknown renderer class: $cls")
    }
}

@JvmInline
private value class Layer<B : OrbitalsRenderer<*>>(
    val renderClass: KClass<B>,
)


/**
 * Return a set of required renderers, keeping pre-existing renderers where possible and creating
 * new renderers when necessary,
 */
inline fun <reified Canvas> diffRenderers(
    existing: RenderSet<Canvas>,
    required: LayerSet,
    options: VisualOptions,
    bodies: List<Body>
): RenderSet<Canvas> {
    val existingLayers = existing.map(::getLayerType).sortedBy { it.ordinal }

    if (existingLayers == required.sortedBy { it.ordinal }) {
        // No change to requirements
        return existing
    }

    val keepLayers = existingLayers.filter { it in required }.toSet()
    val newLayers = required.filter { it !in existingLayers }.toSet()

    val newRenderers = getRenderers<Canvas>(newLayers, options)
    newRenderers.forEach { r -> bodies.forEach { b -> r.onBodyCreated(b) } }

    return (
            existing.filter { getLayerType(it) in keepLayers }
                    + newRenderers
            )
        .toSet()
}

fun getLayerType(renderer: OrbitalsRenderer<*>): RenderLayer = LayerRegistry.getLayerType(renderer)

inline fun <reified Canvas> getRenderers(
    options: VisualOptions,
) = getRenderers<Canvas>(options.renderLayers, options)

@Suppress("UNCHECKED_CAST")
inline fun <reified Canvas> getRenderers(
    layers: LayerSet,
    options: VisualOptions
): RenderSet<Canvas> {
    return when (Canvas::class) {
        AndroidCanvas::class -> getCanvasRenderers(layers, options) as RenderSet<Canvas>
        DrawScope::class -> getComposeRenderers(layers, options) as RenderSet<Canvas>
        else -> setOf()
    }
}

fun getCanvasRenderers(layers: LayerSet, options: VisualOptions): RenderSet<AndroidCanvas> =
    layers.map { getCanvasRenderer(it, options) }.toSet()


fun getComposeRenderers(layers: LayerSet, options: VisualOptions): RenderSet<DrawScope> =
    layers.map { getComposeRenderer(it, options) }.toSet()

@Suppress("UNCHECKED_CAST")
private fun getCanvasRenderer(
    layer: RenderLayer,
    options: VisualOptions
): AndroidRenderer {
    try {
        val renderer = LayerRegistry[layer].renderClass as KClass<out AndroidRenderer>

        return createRenderer(renderer, AndroidCanvasDelegate, options)
    } catch (e: NullPointerException) {
        throw Exception("Unimplemented renderer for layer $layer - did you add it to LayerRegistry? : $e")
    }
}

@Suppress("UNCHECKED_CAST")
private fun getComposeRenderer(
    layer: RenderLayer,
    options: VisualOptions
): ComposeRenderer {
    try {
        val renderer = LayerRegistry[layer].renderClass as KClass<out ComposeRenderer>

        return createRenderer(renderer, ComposeDelegate, options)
    } catch (e: NullPointerException) {
        throw Exception("Unimplemented renderer for layer $layer - did you add it to LayerRegistry? : $e")
    }
}

private fun <Canvas> createRenderer(
    abstractRendererClass: KClass<out OrbitalsRenderer<Canvas>>,
    delegate: CanvasDelegate<Canvas>,
    options: VisualOptions,
): OrbitalsRenderer<Canvas> {
    val constructor = abstractRendererClass.primaryConstructor!!
    val params = constructor.parameters
    val opts = params.find { it.name == "options" }!!
    val del = params.find { it.name == "delegate" }!!

    return constructor.callBy(
        mapOf(
            opts to options,
            del to delegate,
        )
    )
}
