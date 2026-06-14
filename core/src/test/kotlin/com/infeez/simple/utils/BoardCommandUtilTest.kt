package com.infeez.simple.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BoardCommandUtilTest {
    @Test
    fun parseCommand_validCommands_returnsCorrectIndexes() {
        val cases = mapOf(
            "a1" to BoardArrayPosition(0, 7),
            "a8" to BoardArrayPosition(0, 0),
            "h1" to BoardArrayPosition(7, 7),
            "h8" to BoardArrayPosition(7, 0),
            "d4" to BoardArrayPosition(3, 4),
            "e5" to BoardArrayPosition(4, 3),
            "g1" to BoardArrayPosition(6, 7),
        )

        for ((command, expected) in cases) {
            assertEquals(expected, BoardCommandUtil.parseCommand(command))
        }
    }

    @Test
    fun checkerPositionToCommand_validPositions_returnsCorrectCommands() {
        val cases = mapOf(
            BoardArrayPosition(0, 7) to "a1",
            BoardArrayPosition(0, 0) to "a8",
            BoardArrayPosition(7, 7) to "h1",
            BoardArrayPosition(7, 0) to "h8",
            BoardArrayPosition(3, 4) to "d4",
            BoardArrayPosition(4, 3) to "e5",
            BoardArrayPosition(6, 7) to "g1",
        )

        for ((position, expected) in cases) {
            assertEquals(expected, BoardCommandUtil.checkerPositionToCommand(position))
        }
    }

    @Test
    fun roundTrip_allBoardCommands_success() {
        for (file in 'a'..'h') {
            for (rank in '1'..'8') {
                val command = "$file$rank"
                val position = BoardCommandUtil.parseCommand(command)

                assertEquals(command, BoardCommandUtil.checkerPositionToCommand(position))
            }
        }
    }

    @Test
    fun parseCommand_invalidCommands_throwsIllegalArgumentException() {
        val invalidCommands = listOf("", " ", "a", "a9", "i1", "11", "aa")

        for (command in invalidCommands) {
            assertThrows(IllegalArgumentException::class.java) {
                BoardCommandUtil.parseCommand(command)
            }
        }
    }
}
