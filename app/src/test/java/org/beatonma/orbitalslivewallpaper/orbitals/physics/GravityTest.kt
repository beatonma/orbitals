package org.beatonma.orbitalslivewallpaper.orbitals.physics

//class GravityTest {
//    @Test
//    fun testCalculateGravitationalForce() {
//        val massOfEarth = 5.972e24.kg
//        val massOfSun = 1.989e30.kg
//
//        val earthSunDistance = 149_600_000.km
//
//        val force = calculateGravitationalForce(massOfEarth, massOfSun, earthSunDistance)
//
//        force.newtons.shouldbe(3.54e22, 1e20)
//    }
//
//    @Test
//    fun testOrbit() {
//        val earth = Bodies.Earth.copy()
//        val moon = Bodies.Moon.copy()
//
//        val motion = orbit(
//            moon.mass,
//            around = earth,
//            distance = Distances.EarthMoon,
//            angle = 0.degrees
//        )
//
//        // Orbital speed of the Moon is ~1km/s
//        motion.velocity.vector.magnitude.shouldbe(1022.0, 5.0)
//
//        (motion.velocity.x + motion.velocity.y) shouldbe motion.velocity.vector
//
//        // Motion should be tangential to the orbit
//        motion.velocity.y shouldbe motion.velocity.vector
//        motion.velocity.x.magnitude shouldbe 0.0
//
//        motion.velocity.angle.asDegreesInt shouldbe 90
//    }
//}
