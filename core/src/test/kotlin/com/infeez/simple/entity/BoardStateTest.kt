package com.infeez.simple.entity

import com.infeez.simple.state.CheckerColor
import com.infeez.simple.state.CheckerState
import com.infeez.simple.state.GameState
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.Constants.GameEnvTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardStateTest {
    @Test
    fun toGameState_startedBoard_containsTwentyFourCheckers() {
        val state = createStartedBoard().toGameState()

        assertEquals(24, state.board.size)
        assertEquals(CheckerColor.WHITE, state.currentTurn)
        assertEquals(0, state.moveNumber)
    }

    @Test
    fun tryRestoreGameState_validState_restoresBoard() {
        val board = createEmptyBoard()
        val state = GameState(
            board = listOf(
                CheckerState("white-a1", CheckerColor.WHITE, BoardArrayPosition(0, 7)),
                CheckerState("black-b6", CheckerColor.BLACK, BoardArrayPosition(1, 2)),
            ),
            currentTurn = CheckerColor.BLACK,
            moveNumber = 7,
        )

        assertTrue(board.tryRestoreGameState(state))

        assertEquals(GameEnvTypes.WHITE, board.getCell("a1").checker?.type)
        assertEquals(GameEnvTypes.BLACK, board.getCell("b6").checker?.type)
        assertEquals(2, board.toGameState().board.size)
        assertEquals(CheckerColor.BLACK, board.toGameState().currentTurn)
        assertEquals(7, board.toGameState().moveNumber)
    }

    @Test
    fun tryRestoreGameState_checkerOnWhiteCell_returnsFalseAndKeepsExistingBoard() {
        val board = createStartedBoard()
        val originalState = board.toGameState()
        val invalidState = GameState(
            board = listOf(CheckerState("white-a2", CheckerColor.WHITE, BoardArrayPosition(0, 6))),
            currentTurn = CheckerColor.WHITE,
            moveNumber = 1,
        )

        assertFalse(board.tryRestoreGameState(invalidState))

        assertEquals(originalState, board.toGameState())
    }

    @Test
    fun mouseUp_outsideBoard_returnsCheckerToSourceCell() {
        val board = createStartedBoard()

        assertTrue(board.mouseDown(25f, 375f, pointer = 0, mouseButton = 0))
        assertTrue(board.mouseDrag(125f, 275f, pointer = 0))
        assertFalse(board.mouseUp(500f, 500f, pointer = 0, mouseButton = 0))

        assertEquals(GameEnvTypes.WHITE, board.getCell("a1").checker?.type)
    }

    @Test
    fun secondPointer_doesNotStealActiveDrag() {
        val board = createStartedBoard()

        assertTrue(board.mouseDown(25f, 375f, pointer = 0, mouseButton = 0))
        assertFalse(board.mouseDown(75f, 325f, pointer = 1, mouseButton = 0))
        assertFalse(board.mouseDrag(125f, 275f, pointer = 1))
        assertTrue(board.mouseDrag(125f, 275f, pointer = 0))
        assertTrue(board.touchCancelled(125f, 275f, pointer = 0, mouseButton = 0))

        assertEquals(GameEnvTypes.WHITE, board.getCell("a1").checker?.type)
    }

    @Test
    fun touchCancelled_withoutActivePointerDoesNothing() {
        val board = createStartedBoard()

        assertFalse(board.touchCancelled(25f, 375f, pointer = 0, mouseButton = 0))
        assertEquals(GameEnvTypes.WHITE, board.getCell("a1").checker?.type)
    }

    private fun createStartedBoard(): Board {
        return createEmptyBoard().apply {
            startNewGame()
        }
    }

    private fun createEmptyBoard(): Board {
        return Board().apply {
            create()
        }
    }
}
