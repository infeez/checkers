package com.infeez.simple.game.notation

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckersNotationTest {
    @Test
    fun parsePosition_a1_returnsBottomLeftBoardPosition() {
        assertEquals(BoardPosition(0, 7), CheckersNotation.parsePosition("a1").getOrThrow())
    }

    @Test
    fun parsePosition_h8_returnsTopRightBoardPosition() {
        assertEquals(BoardPosition(7, 0), CheckersNotation.parsePosition("h8").getOrThrow())
    }

    @Test
    fun formatPosition_bottomLeftBoardPosition_returnsA1() {
        assertEquals("a1", CheckersNotation.formatPosition(BoardPosition(0, 7)))
    }

    @Test
    fun formatPosition_topRightBoardPosition_returnsH8() {
        assertEquals("h8", CheckersNotation.formatPosition(BoardPosition(7, 0)))
    }

    @Test
    fun parsePosition_invalidInput_returnsFailure() {
        val invalidPositions = listOf("", " ", "i1", "a9", "aa", "a10")

        for (position in invalidPositions) {
            assertTrue("$position should be invalid", CheckersNotation.parsePosition(position).isFailure)
        }
    }

    @Test
    fun parseMove_simpleMove_returnsNotationMove() {
        val move = CheckersNotation.parseMove("a3-b4").getOrThrow()

        assertEquals(BoardPosition(0, 5), move.from)
        assertEquals(BoardPosition(1, 4), move.to)
        assertEquals(MoveType.SIMPLE, move.type)
    }

    @Test
    fun parseMove_captureMove_returnsNotationMove() {
        val move = CheckersNotation.parseMove("c3:e5").getOrThrow()

        assertEquals(BoardPosition(2, 5), move.from)
        assertEquals(BoardPosition(4, 3), move.to)
        assertEquals(MoveType.CAPTURE, move.type)
    }

    @Test
    fun formatMove_simpleMove_usesHyphen() {
        val move = Move(
            from = BoardPosition(0, 5),
            to = BoardPosition(1, 4),
            type = MoveType.SIMPLE,
        )

        assertEquals("a3-b4", CheckersNotation.formatMove(move))
    }

    @Test
    fun formatMove_captureMove_usesColon() {
        val move = Move(
            from = BoardPosition(2, 5),
            to = BoardPosition(4, 3),
            type = MoveType.CAPTURE,
            captured = BoardPosition(3, 4),
        )

        assertEquals("c3:e5", CheckersNotation.formatMove(move))
    }
}
