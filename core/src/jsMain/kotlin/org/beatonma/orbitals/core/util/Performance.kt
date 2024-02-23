package org.beatonma.orbitals.core.util

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()
