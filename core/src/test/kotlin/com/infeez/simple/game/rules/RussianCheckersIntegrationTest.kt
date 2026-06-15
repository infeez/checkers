package com.infeez.simple.game.rules

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RussianCheckersIntegrationTest {
    private val rules = RussianCheckersRules()

    @Test
    fun newGame_whiteMove_thenBlackHasLegalMoves() {
        val initial = rules.createInitialState()
        val whiteMove = Move(
            from = BoardPosition(0, 5),
            to = BoardPosition(1, 4),
            type = MoveType.SIMPLE,
        )

        val result = success(rules.applyMove(initial, whiteMove))

        assertEquals(PlayerColor.BLACK, result.state.currentTurn)
        assertTrue(rules.legalMoves(result.state).isNotEmpty())
    }

    @Test
    fun forcedCapture_captureContinueCaptureFinish_thenTurnChanges() {
        val firstFrom = BoardPosition(2, 5)
        val firstCaptured = BoardPosition(3, 4)
        val firstTarget = BoardPosition(4, 3)
        val secondCaptured = BoardPosition(5, 2)
        val secondTarget = BoardPosition(6, 1)
        val initial = state(
            firstFrom to whiteMan(),
            firstCaptured to blackMan(),
            secondCaptured to blackMan(),
        )

        val afterFirst = success(rules.applyMove(initial, capture(firstFrom, firstTarget, firstCaptured))).state

        assertEquals(PlayerColor.WHITE, afterFirst.currentTurn)
        assertEquals(firstTarget, afterFirst.forcedPiece)

        val afterSecond = success(rules.applyMove(afterFirst, capture(firstTarget, secondTarget, secondCaptured))).state

        assertEquals(PlayerColor.BLACK, afterSecond.currentTurn)
        assertFalse(afterSecond.board.isOccupied(firstCaptured))
        assertFalse(afterSecond.board.isOccupied(secondCaptured))
    }

    @Test
    fun promotionCreatesKingThatCanCapture() {
        val from = BoardPosition(2, 1)
        val promotedAt = BoardPosition(1, 0)
        val blackKing = BoardPosition(7, 6)
        val captured = BoardPosition(4, 3)
        val promoted = success(
            rules.applyMove(
                state(from to whiteMan(), blackKing to blackKing()),
                Move(from = from, to = promotedAt, type = MoveType.SIMPLE),
            ),
        ).state.copy(currentTurn = PlayerColor.WHITE)

        val captureState = promoted.copy(
            board = promoted.board.place(captured, blackMan()),
        )
        val kingCaptures = rules.legalMovesForPiece(captureState, promotedAt)

        assertEquals(Piece(PlayerColor.WHITE, PieceKind.KING), captureState.board.pieceAt(promotedAt))
        assertTrue(kingCaptures.contains(capture(promotedAt, BoardPosition(5, 4), captured)))
    }

    private fun state(vararg pieces: Pair<BoardPosition, Piece>): GameState {
        return GameState(
            board = BoardState(mapOf(*pieces)),
            currentTurn = PlayerColor.WHITE,
        )
    }

    private fun capture(from: BoardPosition, to: BoardPosition, captured: BoardPosition): Move {
        return Move(from = from, to = to, type = MoveType.CAPTURE, captured = captured)
    }

    private fun whiteMan(): Piece = Piece(PlayerColor.WHITE, PieceKind.MAN)

    private fun blackMan(): Piece = Piece(PlayerColor.BLACK, PieceKind.MAN)

    private fun blackKing(): Piece = Piece(PlayerColor.BLACK, PieceKind.KING)

    private fun success(result: MoveResult): MoveResult.Success {
        return result as MoveResult.Success
    }
}
