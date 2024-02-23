import org.beatonma.orbitals.core.physics.Scalar
import kotlin.jvm.JvmInline

@JvmInline
value class Volume(override val value: Float) : Scalar {
    override fun toString(): String = "${value}m³"

    operator fun times(multiplier: Float): Volume = Volume(value * multiplier)
    operator fun div(divider: Float): Volume = Volume(value / divider)
}
