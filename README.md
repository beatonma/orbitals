# Orbitals

Orbitals is an interactive gravity simulator. It is a multiplatform remake of my first Android app:
"Orbitals LWP & Daydream" (2014).

This project is a playground for understanding Kotlin Multiplatform and Jetbrains' multiplatform
version of Compose. Using a single physics engine implementation (see module `orbitals-core`) we are 
ble to build for Android, JVM and Javascript targets. The bulk of rendering code is also shared,
with each target only needing to implement the `CanvasDelegate<T>`interface. This allows us to draw
the simulation to the Android platform Canvas, Compose DrawScope, and Web `<canvas>` context, all
using the same underlying rendering code in `orbitals-render`.

As of Jetbrains Compose version 1.0.0-alpha3, Compose for Web does not (appear to?) support drawing
to Canvas/DrawScope so `orbitals-web` currently uses Kotlin React instead of Compose.
