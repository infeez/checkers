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
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GreedyAiTest {
    private val rules = RussianCheckersRules()
    private val ai = GreedyAi(rules, Random(0))

    @Test
    fun chooseMove_prefersCaptureOverSimpleMove() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)
        val state = state(
            from to whiteMan(),
            captured to blackMan(),
            BoardPosition(0, 5) to whiteMan(),
        )

        val result = ai.chooseMove(request(state))

        assertEquals(capture(from, target, captured), result.move)
    }

    @Test
    fun chooseMove_prefersLongerCaptureChain() {
        val chainFrom = BoardPosition(2, 5)
        val chainFirstCaptured = BoardPosition(3, 4)
        val chainFirstTarget = BoardPosition(4, 3)
        val chainSecondCaptured = BoardPosition(5, 2)
        val singleFrom = BoardPosition(0, 5)
        val singleCaptured = BoardPosition(1, 4)
        val state = state(
            chainFrom to whiteMan(),
            chainFirstCaptured to blackMan(),
            chainSecondCaptured to blackMan(),
            singleFrom to whiteMan(),
            singleCaptured to blackMan(),
        )

        val result = ai.chooseMove(request(state))

        assertEquals(capture(chainFrom, chainFirstTarget, chainFirstCaptured), result.move)
    }

    @Test
    fun chooseMove_prefersPromotionWhenNoCaptureExists() {
        val promotionFrom = BoardPosition(2, 1)
        val state = state(
            promotionFrom to whiteMan(),
            BoardPosition(6, 5) to whiteMan(),
        )

        val result = ai.chooseMove(request(state))

        assertTrue(result.move?.to?.row == 0)
    }

    private fun request(state: GameState): AiMoveRequest {
        return AiMoveRequest(
            gameState = state,
            aiSide = PlayerColor.WHITE,
            difficulty = AiDifficulty.EASY,
            searchLimits = SearchLimits(maxDepth = 1, randomizeEqualMoves = false),
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
