package com.infeez.simple.entity

import com.infeez.simple.utils.Constants.GameEnvTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardMoveTest {
    @Test
    fun moveChecker_legalEmptyBlackTarget_movesChecker() {
        val board = createStartedBoard()

        board.moveChecker("a3", "b4")

        assertNull(board.getCell("a3").checker)
        assertEquals(GameEnvTypes.WHITE, board.getCell("b4").checker?.type)
    }

    @Test
    fun moveChecker_whiteTarget_doesNotMoveChecker() {
        val board = createStartedBoard()

        board.moveChecker("a3", "a4")

        assertEquals(GameEnvTypes.WHITE, board.getCell("a3").checker?.type)
        assertNull(board.getCell("a4").checker)
    }

    @Test
    fun moveChecker_occupiedTarget_doesNotMoveChecker() {
        val board = createStartedBoard()

        board.moveChecker("a3", "b6")

        assertEquals(GameEnvTypes.WHITE, board.getCell("a3").checker?.type)
        assertEquals(GameEnvTypes.BLACK, board.getCell("b6").checker?.type)
    }

    @Test
    fun moveChecker_emptySource_doesNotMoveChecker() {
        val board = createStartedBoard()

        board.moveChecker("b4", "c5")

        assertNull(board.getCell("b4").checker)
        assertNull(board.getCell("c5").checker)
    }

    @Test
    fun moveChecker_invalidCommand_throwsIllegalArgumentException() {
        val board = createStartedBoard()

        assertThrows(IllegalArgumentException::class.java) {
            board.moveChecker("", "b4")
        }
    }

    @Test
    fun resetButtonClick_afterMove_startsNewGame() {
        val board = createStartedBoard()

        board.moveChecker("a3", "b4")
        assertNull(board.getCell("a3").checker)
        assertEquals(GameEnvTypes.WHITE, board.getCell("b4").checker?.type)

        assertTrue(board.mouseDown(20f, 30f, pointer = 0, mouseButton = 0))
        assertTrue(board.mouseUp(20f, 30f, pointer = 0, mouseButton = 0))

        assertEquals(GameEnvTypes.WHITE, board.getCell("a3").checker?.type)
        assertNull(board.getCell("b4").checker)
        assertEquals(0, board.toGameState().moveNumber)
    }

    private fun createStartedBoard(): Board {
        return Board().apply {
            create()
            startNewGame()
        }
    }
}
