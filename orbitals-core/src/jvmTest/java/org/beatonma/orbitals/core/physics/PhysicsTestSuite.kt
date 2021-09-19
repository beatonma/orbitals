package org.beatonma.orbitals.core.physics

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
class PhysicsTestSuite
