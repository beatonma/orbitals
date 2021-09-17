package org.beatonma.orbitals

import org.beatonma.orbitals.engine.EngineTestSuite
import org.beatonma.orbitals.physics.PhysicsTestSuite
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    EngineTestSuite::class,
    PhysicsTestSuite::class,
)
class OrbitalsTestSuite
