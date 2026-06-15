package com.infeez.simple.game.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardPositionTest {
    @Test
    fun isInsideBoard_positionWithinEightByEight_returnsTrue() {
        assertTrue(BoardPosition(0, 0).isInsideBoard())
        assertTrue(BoardPosition(7, 7).isInsideBoard())
    }

    @Test
    fun isInsideBoard_positionOutsideEightByEight_returnsFalse() {
        assertFalse(BoardPosition(-1, 0).isInsideBoard())
        assertFalse(BoardPosition(0, -1).isInsideBoard())
        assertFalse(BoardPosition(8, 0).isInsideBoard())
        assertFalse(BoardPosition(0, 8).isInsideBoard())
    }

    @Test
    fun isDarkSquare_darkBoardSquare_returnsTrue() {
        assertTrue(BoardPosition(0, 7).isDarkSquare())
        assertTrue(BoardPosition(1, 0).isDarkSquare())
    }

    @Test
    fun isDarkSquare_lightOrOutsideBoardSquare_returnsFalse() {
        assertFalse(BoardPosition(0, 0).isDarkSquare())
        assertFalse(BoardPosition(-1, 0).isDarkSquare())
    }
}
