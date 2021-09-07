package org.beatonma.orbitalslivewallpaper.orbitals.physics

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds


@OptIn(ExperimentalTime::class)
val Long.ms: Duration
    get() = Duration.milliseconds(this)

@OptIn(ExperimentalTime::class)
val Int.ms: Duration
    get() = this.toLong().ms
