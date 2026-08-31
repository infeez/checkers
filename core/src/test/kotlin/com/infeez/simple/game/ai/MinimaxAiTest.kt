package com.infeez.simple.game.ai

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.RussianCheckersRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MinimaxAiTest {
    private val rules = RussianCheckersRules()
    private val ai = MinimaxAi(
        rules = rules,
        evaluator = BasicBoardEvaluator(),
        moveOrdering = DefaultMoveOrdering(rules),
    )

    @Test
    fun chooseMove_selectsImmediateWinningCapture() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)
        val state = state(from to whiteMan(), captured to blackMan())

        val result = ai.chooseMove(request(state))

        assertEquals(capture(from, target, captured), result.move)
        assertTrue(result.searchedNodes > 0)
    }

    @Test
    fun chooseMove_withoutLegalMoves_returnsNull() {
        val state = GameState(
            board = BoardState(),
            currentTurn = PlayerColor.WHITE,
        )

        assertNull(ai.chooseMove(request(state)).move)
    }

    @Test
    fun chooseMove_wrongSide_returnsNull() {
        val state = rules.createInitialState()

        assertNull(ai.chooseMove(request(state, aiSide = PlayerColor.BLACK)).move)
    }

    @Test
    fun chooseMove_respectsMandatoryCapture() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val state = state(
            from to whiteMan(),
            captured to blackMan(),
            BoardPosition(0, 5) to whiteMan(),
        )

        val result = ai.chooseMove(request(state))

        assertEquals(MoveType.CAPTURE, result.move?.type)
    }

    private fun request(state: GameState, aiSide: PlayerColor = PlayerColor.WHITE): AiMoveRequest {
        return AiMoveRequest(
            gameState = state,
            aiSide = aiSide,
            difficulty = AiDifficulty.NORMAL,
            searchLimits = SearchLimits(maxDepth = 3, maxTimeMillis = null, randomizeEqualMoves = false),
        )
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
}
