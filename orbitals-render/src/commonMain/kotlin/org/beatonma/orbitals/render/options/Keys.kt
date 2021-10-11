package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import kotlin.jvm.JvmInline

sealed interface Key<T> {
    val key: String
}

@JvmInline value class StringKey<E: Enum<E>>(override val key: String): Key<String>
@JvmInline value class StringSetKey<E: Enum<E>>(override val key: String): Key<Set<String>>
@JvmInline value class FloatKey(override val key: String): Key<Float>
@JvmInline value class IntKey(override val key: String): Key<Int>
@JvmInline value class BooleanKey(override val key: String): Key<Boolean>

object VisualKey {
    val DrawStyle = StringKey<DrawStyle>("style")
    val TraceLength = IntKey("path_history_length")
    val StrokeWidth = FloatKey("stroke_width")
    val RenderLayers = StringSetKey<RenderLayer>("layers")
    val BodyScale = FloatKey("body_scale")
}

object ColorKey {
    val BackgroundColor = IntKey("background")
    val Colors = StringSetKey<ObjectColors>("colors")
    val BodyAlpha = FloatKey("alpha")
}

object PhysicsKey {
    val Debug = BooleanKey("debugging")
    val MaxFixedBodyAgeSeconds = IntKey("max_fixedbody_age_seconds")
    val Generators = StringSetKey<SystemGenerator>("generators")
    val AutoAddBodies = BooleanKey("auto")
    val GravityMultiplier = FloatKey("gravity")
    val MaxEntities = IntKey("max")
    val CollisionStyle = StringKey<CollisionStyle>("collision")
}
