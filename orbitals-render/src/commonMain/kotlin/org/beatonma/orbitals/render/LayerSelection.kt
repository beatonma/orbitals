package org.beatonma.orbitals.render

import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.renderer.AccelerationRenderer
import org.beatonma.orbitals.render.renderer.DripRenderer
import org.beatonma.orbitals.render.renderer.SimpleRenderer
import org.beatonma.orbitals.render.renderer.TrailRenderer
import kotlin.reflect.KClass

private typealias LayerSet = Set<RenderLayer>
private typealias RenderSet<Canvas> = Set<OrbitalsRenderer<Canvas>>


/**
 * Layers must be registered here!
 */
private class LayerRegistry<Canvas> {
    private val registry: Map<RenderLayer, LayerRenderer<Canvas>> = mapOf(
        RenderLayer.Default to LayerRenderer(SimpleRenderer::class, ::SimpleRenderer),
        RenderLayer.Trails to LayerRenderer(TrailRenderer::class, ::TrailRenderer),
        RenderLayer.Acceleration to LayerRenderer(AccelerationRenderer::class, ::AccelerationRenderer),
        RenderLayer.Drip to LayerRenderer(DripRenderer::class, ::DripRenderer),
    )

    operator fun get(key: RenderLayer) = registry[key]!!

    fun getLayerType(renderer: OrbitalsRenderer<Canvas>): RenderLayer {
        val cls = renderer::class

        registry.forEach { (renderLayer, renderer) ->
            if (cls == renderer.cls) return renderLayer
        }

        throw IllegalArgumentException("Unknown renderer class: $cls")
    }
}

private data class LayerRenderer<Canvas>(
    val cls: KClass<*>,
    val factory: (delegate: CanvasDelegate<Canvas>, options: VisualOptions) -> OrbitalsRenderer<Canvas>
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

fun <Canvas> getLayerType(renderer: OrbitalsRenderer<Canvas>): RenderLayer =
    LayerRegistry<Canvas>().getLayerType(renderer)

fun <Canvas> getRenderers(
    options: VisualOptions,
    delegate: CanvasDelegate<Canvas>
) = getRenderers(options.renderLayers, options, delegate)

fun <Canvas> getRenderers(
    layers: LayerSet,
    options: VisualOptions,
    delegate: CanvasDelegate<Canvas>,
): RenderSet<Canvas> {
    return layers.map { layer ->
        LayerRegistry<Canvas>()[layer].factory(delegate, options)
    }.toSet()
}
