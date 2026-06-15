package com.infeez.simple.game.rules

import com.infeez.simple.game.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InitialBoardFactoryTest {
    @Test
    fun russianCheckers_createsTwentyFourPieces() {
        val board = InitialBoardFactory.russianCheckers()

        assertEquals(24, board.pieces.size)
    }

    @Test
    fun russianCheckers_createsTwelvePiecesForEachSide() {
        val board = InitialBoardFactory.russianCheckers()

        assertEquals(12, board.piecesOf(PlayerColor.WHITE).size)
        assertEquals(12, board.piecesOf(PlayerColor.BLACK).size)
    }

    @Test
    fun russianCheckers_placesAllPiecesOnDarkSquares() {
        val board = InitialBoardFactory.russianCheckers()

        assertTrue(board.pieces.keys.all { position -> position.isDarkSquare() })
    }

    @Test
    fun russianCheckers_placesWhiteOnBottomThreeRowsAndBlackOnTopThreeRows() {
        val board = InitialBoardFactory.russianCheckers()

        assertTrue(board.positionsOf(PlayerColor.WHITE).all { position -> position.row in 5..7 })
        assertTrue(board.positionsOf(PlayerColor.BLACK).all { position -> position.row in 0..2 })
    }

    @Test
    fun russianCheckers_leavesMiddleTwoRowsEmpty() {
        val board = InitialBoardFactory.russianCheckers()

        assertTrue(board.pieces.keys.none { position -> position.row in 3..4 })
    }
}
