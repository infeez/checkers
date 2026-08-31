package com.infeez.simple.game.ai

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.CheckersRules
import kotlin.random.Random

class GreedyAi(
    private val rules: CheckersRules,
    private val random: Random = Random.Default,
) : CheckersAi {
    override fun chooseMove(request: AiMoveRequest): AiMoveResult {
        if (request.gameState.status != GameStatus.InProgress || request.gameState.currentTurn != request.aiSide) {
            return AiMoveResult(move = null, depth = request.searchLimits.maxDepth)
        }

        val moves = rules.legalMoves(request.gameState)
        if (moves.isEmpty()) {
            return AiMoveResult(move = null, depth = request.searchLimits.maxDepth)
        }

        val scoredMoves = moves.map { move -> move to scoreMove(request, move) }
        val bestScore = scoredMoves.maxOf { (_, score) -> score }
        val bestMoves = scoredMoves
            .filter { (_, score) -> score == bestScore }
            .map { (move, _) -> move }
        val selected = if (request.searchLimits.randomizeEqualMoves) {
            bestMoves.random(random)
        } else {
            bestMoves.first()
        }

        return AiMoveResult(
            move = selected,
            score = bestScore,
            searchedNodes = moves.size,
            depth = request.searchLimits.maxDepth,
        )
    }

    private fun scoreMove(request: AiMoveRequest, move: Move): Int {
        val state = request.gameState
        val movedPiece = state.board.pieceAt(move.from)
        val applied = rules.applyMove(state, move) as? MoveResult.Success ?: return Int.MIN_VALUE
        val captureScore = captureChainCount(state, move) * CAPTURE_VALUE
        val kingCaptureScore = move.captured
            ?.let(state.board::pieceAt)
            ?.takeIf { piece -> piece.kind == PieceKind.KING }
            ?.let { KING_CAPTURE_BONUS }
            ?: 0
        val promotionScore = if (
            applied.appliedMove.movedPieceBefore.kind == PieceKind.MAN &&
            applied.appliedMove.movedPieceAfter.kind == PieceKind.KING
        ) {
            PROMOTION_VALUE
        } else {
            0
        }
        val vulnerablePenalty = if (isMovedPieceImmediatelyCapturable(applied.state, move.to, request.aiSide)) {
            VULNERABLE_PENALTY
        } else {
            0
        }
        val advancementScore = movedPiece?.let { piece ->
            if (piece.kind != PieceKind.MAN) {
                0
            } else {
                val direction = when (piece.color) {
                    PlayerColor.WHITE -> move.from.row - move.to.row
                    PlayerColor.BLACK -> move.to.row - move.from.row
                }
                direction * ADVANCEMENT_VALUE
            }
        } ?: 0

        return captureScore + kingCaptureScore + promotionScore + vulnerablePenalty + advancementScore
    }

    private fun captureChainCount(state: com.infeez.simple.game.model.GameState, move: Move): Int {
        if (move.type != MoveType.CAPTURE) {
            return 0
        }
        val result = rules.applyMove(state, move) as? MoveResult.Success ?: return 0
        if (result.state.currentTurn != state.currentTurn || result.state.forcedPiece == null) {
            return 1
        }

        val continuationCaptures = rules.legalMoves(result.state)
            .filter { continuation -> continuation.type == MoveType.CAPTURE }
        if (continuationCaptures.isEmpty()) {
            return 1
        }

        return 1 + continuationCaptures.maxOf { continuation ->
            captureChainCount(result.state, continuation)
        }
    }

    private fun isMovedPieceImmediatelyCapturable(
        state: com.infeez.simple.game.model.GameState,
        movedTo: BoardPosition,
        side: PlayerColor,
    ): Boolean {
        if (state.currentTurn == side || state.status != GameStatus.InProgress) {
            return false
        }

        return rules.legalMoves(state).any { move ->
            move.type == MoveType.CAPTURE && move.captured == movedTo
        }
    }

    private companion object {
        const val CAPTURE_VALUE = 100
        const val KING_CAPTURE_BONUS = 150
        const val PROMOTION_VALUE = 80
        const val VULNERABLE_PENALTY = -70
        const val ADVANCEMENT_VALUE = 10
    }
}
