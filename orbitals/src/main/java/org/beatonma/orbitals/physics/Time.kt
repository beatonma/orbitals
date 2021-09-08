package org.beatonma.orbitals.physics

import kotlin.time.Duration
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
val Long.ms: Duration
    get() = Duration.milliseconds(this)

@OptIn(ExperimentalTime::class)
val Int.ms: Duration
    get() = this.toLong().ms
