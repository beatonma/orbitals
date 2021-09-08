package org.beatonma.orbitalslivewallpaper

import org.beatonma.orbitalslivewallpaper.orbitals.physics.PositionTest
import org.beatonma.orbitalslivewallpaper.orbitals.physics.TrigonometryTest
import org.beatonma.orbitalslivewallpaper.orbitals.physics.VelocityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    PositionTest::class,
    TrigonometryTest::class,
    VelocityTest::class,
)
class OrbitalsTestSuite
