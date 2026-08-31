package com.infeez.simple.game.ai

import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.CheckersRules

class MinimaxAi(
    private val rules: CheckersRules,
    private val evaluator: BoardEvaluator,
    private val moveOrdering: MoveOrdering = DefaultMoveOrdering(rules),
) : CheckersAi {
    override fun chooseMove(request: AiMoveRequest): AiMoveResult {
        if (request.gameState.status != GameStatus.InProgress || request.gameState.currentTurn != request.aiSide) {
            return AiMoveResult(move = null, depth = request.searchLimits.maxDepth)
        }

        val moves = moveOrdering.orderMoves(request.gameState, request.aiSide, rules.legalMoves(request.gameState))
        if (moves.isEmpty()) {
            return AiMoveResult(move = null, depth = request.searchLimits.maxDepth)
        }

        val deadline = request.searchLimits.maxTimeMillis?.let { limit ->
            System.currentTimeMillis() + limit
        }
        val search = SearchContext(deadline = deadline)
        var bestMove = moves.first()
        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        for (move in moves) {
            if (search.timeLimitExceeded()) {
                break
            }
            val nextState = applyMoveOrNull(request.gameState, move) ?: continue
            val nextDepth = nextDepth(request.gameState, nextState, request.searchLimits.maxDepth)
            val score = minimax(
                state = nextState,
                depth = nextDepth,
                aiSide = request.aiSide,
                alpha = alpha,
                beta = beta,
                search = search,
            )
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
            alpha = maxOf(alpha, bestScore)
        }

        return AiMoveResult(
            move = bestMove,
            score = bestScore,
            searchedNodes = search.nodes,
            depth = request.searchLimits.maxDepth,
        )
    }

    private fun minimax(
        state: GameState,
        depth: Int,
        aiSide: PlayerColor,
        alpha: Int,
        beta: Int,
        search: SearchContext,
    ): Int {
        search.nodes++
        if (state.status != GameStatus.InProgress || depth <= 0 || search.timeLimitExceeded()) {
            return evaluator.evaluate(state, aiSide)
        }

        val moves = moveOrdering.orderMoves(state, state.currentTurn, rules.legalMoves(state))
        if (moves.isEmpty()) {
            return evaluator.evaluate(state, aiSide)
        }

        val maximizing = state.currentTurn == aiSide
        var currentAlpha = alpha
        var currentBeta = beta

        return if (maximizing) {
            var bestScore = Int.MIN_VALUE
            for (move in moves) {
                val nextState = applyMoveOrNull(state, move) ?: continue
                val score = minimax(
                    state = nextState,
                    depth = nextDepth(state, nextState, depth),
                    aiSide = aiSide,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    search = search,
                )
                bestScore = maxOf(bestScore, score)
                currentAlpha = maxOf(currentAlpha, bestScore)
                if (currentBeta <= currentAlpha || search.timeLimitExceeded()) {
                    break
                }
            }
            bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (move in moves) {
                val nextState = applyMoveOrNull(state, move) ?: continue
                val score = minimax(
                    state = nextState,
                    depth = nextDepth(state, nextState, depth),
                    aiSide = aiSide,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    search = search,
                )
                bestScore = minOf(bestScore, score)
                currentBeta = minOf(currentBeta, bestScore)
                if (currentBeta <= currentAlpha || search.timeLimitExceeded()) {
                    break
                }
            }
            bestScore
        }
    }

    private fun nextDepth(before: GameState, after: GameState, currentDepth: Int): Int {
        return if (after.currentTurn == before.currentTurn) {
            currentDepth
        } else {
            currentDepth - 1
        }
    }

    private fun applyMoveOrNull(state: GameState, move: Move): GameState? {
        return (rules.applyMove(state, move) as? MoveResult.Success)?.state
    }

    private class SearchContext(
        private val deadline: Long?,
    ) {
        var nodes: Int = 0

        fun timeLimitExceeded(): Boolean {
            return deadline != null && System.currentTimeMillis() >= deadline
        }
    }
}
