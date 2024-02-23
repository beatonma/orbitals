package org.beatonma.orbitals.render.interaction

import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.PhysicsKeys
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualKeys

private const val DensityStepSize = .05f


class OrbitalsKeyboardHandler(
    private val engine: OrbitalsRenderEngine<*>,
    private val persistence: OptionPersistence,
) {
    private val options: Options get() = engine.options
    private fun toggleLayer(layer: RenderLayer) = hotkey {
        persistence.updateOption(
            VisualKeys.RenderLayers,
            options.visualOptions.renderLayers.toggle(layer)
        )
    }

    private fun setCollisionStyle(style: CollisionStyle) = hotkey {
        persistence.updateOption(PhysicsKeys.CollisionStyle, style)
    }

    private fun increaseSize() = hotkey {
        persistence.updateOption(
            PhysicsKeys.Density,
            (options.physics.bodyDensity.value - DensityStepSize).coerceAtLeast(0.01f)
        )
    }

    private fun decreaseSize() = hotkey {
        persistence.updateOption(
            PhysicsKeys.Density,
            options.physics.bodyDensity.value + DensityStepSize
        )
    }

    fun onKeyDown(key: Key): Boolean =
        when (key) {
            Key.Delete, Key.Backspace -> hotkey(engine::clear)
            Key.Insert, Key.Spacebar -> hotkey(engine::addBodies)

            // Render lays
            Key.One -> toggleLayer(RenderLayer.entries[0])
            Key.Two -> toggleLayer(RenderLayer.entries[1])
            Key.Three -> toggleLayer(RenderLayer.entries[2])

            // Collisions
            Key.Q -> setCollisionStyle(CollisionStyle.entries[0])
            Key.W -> setCollisionStyle(CollisionStyle.entries[1])
            Key.E -> setCollisionStyle(CollisionStyle.entries[2])
            Key.R -> setCollisionStyle(CollisionStyle.entries[3])
            Key.T -> setCollisionStyle(CollisionStyle.entries[4])

            // Density (-> rendered size)
            Key.LeftBracket, Key.Add -> increaseSize()
            Key.RightBracket, Key.Subtract -> decreaseSize()

            else -> false
        }
}

private inline fun hotkey(block: () -> Unit): Boolean = block().run { true }
private fun <T> Set<T>.toggle(value: T): Set<T> = when (value) {
    in this -> this - value
    else -> this + value
}

enum class Key {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Zero,
    Escape,
    Grave,
    Delete,
    Backspace,
    Insert,
    Spacebar,
    LeftBracket,
    RightBracket,
    Add,
    Subtract,
    ;
}
