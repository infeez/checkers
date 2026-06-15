package com.infeez.simple.entity

import com.infeez.simple.state.CheckerColor
import com.infeez.simple.state.CheckerState
import com.infeez.simple.state.GameState
import com.infeez.simple.utils.BoardArrayPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WinnerTest {
    @Test
    fun checkWinner_bothSidesPresent_returnsNone() {
        val board = createStartedBoard()

        assertEquals(Winner.NONE, board.checkWinner())
    }

    @Test
    fun checkWinner_noWhiteCheckers_returnsBlack() {
        val board = createStartedBoard()

        assertTrue(
            board.tryRestoreGameState(
                GameState(
                    board = listOf(
                        CheckerState("black-b6", CheckerColor.BLACK, BoardArrayPosition(1, 2)),
                    ),
                    currentTurn = CheckerColor.WHITE,
                    moveNumber = 0,
                ),
            ),
        )

        assertEquals(Winner.BLACK, board.checkWinner())
    }

    @Test
    fun checkWinner_noBlackCheckers_returnsWhite() {
        val board = createStartedBoard()

        assertTrue(
            board.tryRestoreGameState(
                GameState(
                    board = listOf(
                        CheckerState("white-a1", CheckerColor.WHITE, BoardArrayPosition(0, 7)),
                    ),
                    currentTurn = CheckerColor.BLACK,
                    moveNumber = 0,
                ),
            ),
        )

        assertEquals(Winner.WHITE, board.checkWinner())
    }

    private fun createStartedBoard(): Board {
        return Board().apply {
            create()
            startNewGame()
        }
    }
}
