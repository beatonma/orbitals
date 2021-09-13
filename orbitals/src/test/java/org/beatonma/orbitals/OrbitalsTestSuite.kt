package org.beatonma.orbitals

import org.beatonma.orbitals.physics.BodyTest
import org.beatonma.orbitals.physics.MathTest
import org.beatonma.orbitals.physics.PositionTest
import org.beatonma.orbitals.physics.TangentTest
import org.beatonma.orbitals.physics.AngleTest
import org.beatonma.orbitals.physics.VelocityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    AngleTest::class,
    BodyTest::class,
    MathTest::class,
    PositionTest::class,
    TangentTest::class,
    VelocityTest::class,
)
class OrbitalsTestSuite
