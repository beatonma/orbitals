package org.beatonma.orbitals.core.physics

import kotlin.jvm.JvmInline

fun uniqueID(name: Any): UniqueID = UniqueID("$name[$uniqueID]")
expect val uniqueID: String

@JvmInline
value class UniqueID internal constructor(val value: String) {
    override fun toString(): String {
        return "id:$value"
    }
}
