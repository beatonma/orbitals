package org.beatonma.orbitals.core.physics

// Constructors from Number values to keep things as terse as possible
internal fun Direction(x: Number, y: Number) = Direction(x.metres, y.metres)
internal fun Momentum(x: Number, y: Number) = Momentum(MomentumScalar(x), MomentumScalar(y))
internal fun MomentumScalar(value: Number) = MomentumScalar(value.toFloat())
internal fun Position(x: Number, y: Number) = Position(x.metres, y.metres)
internal fun Speed(value: Number) = Speed(value.toFloat())
internal fun Velocity(x: Number, y: Number) = Velocity(Speed(x), Speed(y))

internal val Number.kg: Mass get() = this.toFloat().kg
internal val Number.degrees: Angle get() = toFloat().degrees
internal val Number.rawDegrees: Angle get() = this.toFloat().rawDegrees
internal val Number.metres: Distance get() = this.toFloat().metres
