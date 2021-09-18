package org.beatonma.orbitals.rendering

import org.beatonma.orbitals.options.RenderLayer
import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.rendering.renderer.AccelerationRenderer
import org.beatonma.orbitals.rendering.renderer.DripRenderer
import org.beatonma.orbitals.rendering.renderer.SimpleRenderer
import org.beatonma.orbitals.rendering.renderer.TrailRenderer
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

private typealias LayerSet = Set<RenderLayer>
private typealias RenderSet<Canvas> = Set<OrbitalsRenderer<Canvas>>


/**
 * Layers must be registered here!
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
    bodies: List<Body>,
    delegate: CanvasDelegate<Canvas>,
): RenderSet<Canvas> {
    val existingLayers = existing.map(::getLayerType).sortedBy { it.ordinal }

    if (existingLayers == required.sortedBy { it.ordinal }) {
        // No change to requirements
        return existing
    }

    val keepLayers = existingLayers.filter { it in required }.toSet()
    val newLayers = required.filter { it !in existingLayers }.toSet()

    val newRenderers = getRenderers(newLayers, options, delegate)
    newRenderers.forEach { r -> bodies.forEach { b -> r.onBodyCreated(b) } }

    return (
            existing.filter { getLayerType(it) in keepLayers }
                    + newRenderers
            )
        .toSet()
}

fun getLayerType(renderer: OrbitalsRenderer<*>): RenderLayer = LayerRegistry.getLayerType(renderer)

fun <Canvas> getRenderers(
    options: VisualOptions,
    delegate: CanvasDelegate<Canvas>
) = getRenderers(options.renderLayers, options, delegate)

@Suppress("UNCHECKED_CAST")
fun <Canvas> getRenderers(
    layers: LayerSet,
    options: VisualOptions,
    delegate: CanvasDelegate<Canvas>,
): RenderSet<Canvas> {
    return layers.map { layer ->
        createRenderer(
            LayerRegistry[layer].renderClass as KClass<out OrbitalsRenderer<Canvas>>,
            delegate,
            options,
        )
    }.toSet()
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
