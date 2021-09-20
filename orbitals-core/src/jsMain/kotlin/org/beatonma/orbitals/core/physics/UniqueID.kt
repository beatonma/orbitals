package org.beatonma.orbitals.core.physics

import kotlin.random.Random

actual val uniqueID: String get() = Random.nextInt(10_000, 10_000_000).toString()
