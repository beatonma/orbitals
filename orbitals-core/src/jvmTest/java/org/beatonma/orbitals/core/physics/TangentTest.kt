package org.beatonma.orbitals.core.physics

import org.beatonma.orbitals.test.shouldbe
import org.junit.Test

class TangentTest {
    private infix fun Float.shouldbe(expected: Float) {
        this.shouldbe(expected, 0.01f)
    }

    @Test
    fun getRadialPosition_isCorrect() {
        fun check(parent: Position, distance: Distance, angle: Angle) {
            val radial = getRadialPosition(parent, distance = distance, angle = angle)
            parent.angleTo(radial).value shouldbe angle.value
            parent.distanceTo(radial).value shouldbe distance.value
        }

        check(Position(30, 40), 10.metres, 0.degrees)
        check(Position(30, 40), 20.metres, 30.degrees)
        check(Position(30, 40), 15.metres, 271.degrees)
        check(Position(30, 40), 5.metres, 357.degrees)

        val parent = Position(30, 40)
        val radial = getRadialPosition(parent, distance = 10.metres, angle = 0.degrees)
        parent.angleTo(radial) shouldbe 0.degrees
        parent.distanceTo(radial) shouldbe 10.metres
    }

    @Test
    fun doAround_isCorrect() {
        val center = ZeroPosition
        val result = mutableListOf<Position>()

        doAround(
            radius = 1.metres,
            center = center,
            steps = 8,
        ) { deg, x, y ->
            result.add(Position(x, y))
            println("$deg: $x, $y")
        }

        val diagonal = kotlin.math.sqrt(2f) / 2f

        result.size shouldbe 8
        val expected = listOf(
            Position(1, 0),
            Position(diagonal, diagonal),
            Position(0, 1),
            Position(-diagonal, diagonal),
            Position(-1, 0),
            Position(-diagonal, -diagonal),
            Position(0, -1),
            Position(diagonal, -diagonal),
        )

        result.zip(expected).forEach { (r, e) ->
            r shouldbe e
        }
    }

    @Test
    fun mapAround_isCorrect() {
        val center = ZeroPosition

        val result = mapAround(
            radius = 1.metres,
            center = center,
            steps = 8,
        ) { deg, x, y ->
            Position(x, y)
        }

        val diagonal = sin(45.degrees)

        result.size shouldbe 8
        val expected = listOf(
            Position(1, 0),
            Position(diagonal, diagonal),
            Position(0, 1),
            Position(-diagonal, diagonal),
            Position(-1, 0),
            Position(-diagonal, -diagonal),
            Position(0, -1),
            Position(diagonal, -diagonal),
        )

        result.zip(expected).forEach { (r, e) ->
            r shouldbe e
        }
    }
}
