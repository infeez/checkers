package com.infeez.simple.game.ai

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.model.WinReason
import com.infeez.simple.game.rules.RussianCheckersRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BasicBoardEvaluatorTest {
    private val evaluator = BasicBoardEvaluator()

    @Test
    fun evaluate_initialPosition_isBalanced() {
        val state = RussianCheckersRules().createInitialState()

        assertEquals(0, evaluator.evaluate(state, PlayerColor.WHITE))
    }

    @Test
    fun evaluate_extraPieceGivesAdvantage() {
        val state = state(
            BoardPosition(0, 7) to Piece(PlayerColor.WHITE, PieceKind.MAN),
            BoardPosition(1, 0) to Piece(PlayerColor.BLACK, PieceKind.MAN),
            BoardPosition(2, 7) to Piece(PlayerColor.WHITE, PieceKind.MAN),
        )

        assertTrue(evaluator.evaluate(state, PlayerColor.WHITE) > 0)
        assertTrue(evaluator.evaluate(state, PlayerColor.BLACK) < 0)
    }

    @Test
    fun evaluate_kingIsWorthMoreThanMan() {
        val manState = state(BoardPosition(0, 7) to Piece(PlayerColor.WHITE, PieceKind.MAN))
        val kingState = state(BoardPosition(0, 7) to Piece(PlayerColor.WHITE, PieceKind.KING))

        assertTrue(evaluator.evaluate(kingState, PlayerColor.WHITE) > evaluator.evaluate(manState, PlayerColor.WHITE))
    }

    @Test
    fun evaluate_winAndLossUseLargeScores() {
        val state = GameState(
            board = BoardState(),
            currentTurn = PlayerColor.BLACK,
            status = GameStatus.Winner(PlayerColor.WHITE, WinReason.OPPONENT_HAS_NO_PIECES),
        )

        assertTrue(evaluator.evaluate(state, PlayerColor.WHITE) >= 10_000)
        assertTrue(evaluator.evaluate(state, PlayerColor.BLACK) <= -10_000)
    }

    @Test
    fun evaluate_whiteAndBlackPerspectiveAreOpposite() {
        val state = state(
            BoardPosition(0, 7) to Piece(PlayerColor.WHITE, PieceKind.MAN),
            BoardPosition(1, 0) to Piece(PlayerColor.BLACK, PieceKind.MAN),
            BoardPosition(2, 7) to Piece(PlayerColor.WHITE, PieceKind.KING),
        )

        assertEquals(
            evaluator.evaluate(state, PlayerColor.WHITE),
            -evaluator.evaluate(state, PlayerColor.BLACK),
        )
    }

    private fun state(vararg pieces: Pair<BoardPosition, Piece>): GameState {
        return GameState(
            board = BoardState(mapOf(*pieces)),
            currentTurn = PlayerColor.WHITE,
        )
    }
}
