@file:JvmName("UniqueIDJvm")
package org.beatonma.orbitals.core.physics

import java.util.UUID

actual val uniqueID: String get() = UUID.randomUUID().toString().substring(0, 5)
