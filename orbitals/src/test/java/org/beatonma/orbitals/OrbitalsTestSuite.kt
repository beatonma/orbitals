package org.beatonma.orbitals

import org.beatonma.orbitals.physics.PositionTest
import org.beatonma.orbitals.physics.TrigonometryTest
import org.beatonma.orbitals.physics.VelocityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    PositionTest::class,
    TrigonometryTest::class,
    VelocityTest::class,
)
class OrbitalsTestSuite
