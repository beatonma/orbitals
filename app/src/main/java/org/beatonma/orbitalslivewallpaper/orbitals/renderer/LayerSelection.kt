package org.beatonma.orbitalslivewallpaper.orbitals.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.debug
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.options.RenderLayer
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import android.graphics.Canvas as AndroidCanvas
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.SimpleRenderer as CanvasSimpleRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.TrailRenderer as CanvasTrailRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.SimpleRenderer as ComposeSimpleRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.TrailRenderer as ComposeTrailRenderer

private typealias LayerSet = Set<RenderLayer>
private typealias RenderSet<Canvas> = Set<OrbitalsRenderer<Canvas>>

inline fun <reified Canvas> diffRenderers(
    engine: OrbitalsRenderEngine<Canvas>,
) = diffRenderers(
    engine.renderers,
    engine.options.visualOptions.renderLayers,
    engine.options.visualOptions,
    engine.bodies,
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
        debug("No layer changes")
        return existing
    }

    val keepLayers = existingLayers.filter { it in required }.toSet()
    val newLayers = required.filter { it !in existingLayers }.toSet()

    debug("keep $keepLayers")
    debug("add $newLayers")

    val newRenderers = getRenderers<Canvas>(newLayers, options)
    newRenderers.forEach { r -> bodies.forEach { b -> r.onBodyCreated(b) } }

    return (
            existing.filter { getLayerType(it) in keepLayers }
                    + newRenderers
            )
        .toSet()
}

fun getLayerType(renderer: OrbitalsRenderer<*>): RenderLayer {
    val cls = renderer::class.java
    return when {
        BaseSimpleRenderer::class.java.isAssignableFrom(cls) -> RenderLayer.Default
        BaseTrailRenderer::class.java.isAssignableFrom(cls) -> RenderLayer.Trails
        else -> throw IllegalArgumentException("Unknown renderer class: $cls")
    }
}

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

private fun getCanvasRenderer(
    layer: RenderLayer,
    options: VisualOptions
): OrbitalsRenderer<AndroidCanvas> {
    return when (layer) {
        RenderLayer.Default -> CanvasSimpleRenderer(options)
        RenderLayer.Trails -> CanvasTrailRenderer(options)
        else -> throw Exception("Unimplemented renderer: $layer")
    }
}

fun getComposeRenderers(layers: LayerSet, options: VisualOptions): RenderSet<DrawScope> =
    layers.map { getComposeRenderer(it, options) }.toSet()

private fun getComposeRenderer(
    layer: RenderLayer,
    options: VisualOptions
): OrbitalsRenderer<DrawScope> {
    return when (layer) {
        RenderLayer.Default -> ComposeSimpleRenderer(options)
        RenderLayer.Trails -> ComposeTrailRenderer(options)
        else -> throw Exception("Unimplemented renderer: $layer")
    }
}
