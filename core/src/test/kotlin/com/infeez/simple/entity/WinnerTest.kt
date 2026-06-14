package com.infeez.simple.entity

import com.infeez.simple.utils.Constants.GameEnvTypes
import org.junit.Assert.assertEquals
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

        board.cells.clearCheckers(GameEnvTypes.WHITE)

        assertEquals(Winner.BLACK, board.checkWinner())
    }

    @Test
    fun checkWinner_noBlackCheckers_returnsWhite() {
        val board = createStartedBoard()

        board.cells.clearCheckers(GameEnvTypes.BLACK)

        assertEquals(Winner.WHITE, board.checkWinner())
    }

    private fun createStartedBoard(): Board {
        return Board().apply {
            create()
            startNewGame()
        }
    }
}
