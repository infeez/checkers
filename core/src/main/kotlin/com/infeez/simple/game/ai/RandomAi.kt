package com.infeez.simple.game.ai

import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.rules.CheckersRules
import kotlin.random.Random

class RandomAi(
    private val rules: CheckersRules,
    private val random: Random = Random.Default,
) : CheckersAi {
    override fun chooseMove(request: AiMoveRequest): AiMoveResult {
        if (request.gameState.status != GameStatus.InProgress || request.gameState.currentTurn != request.aiSide) {
            return AiMoveResult(move = null, depth = 0)
        }

        val moves = rules.legalMoves(request.gameState)
        if (moves.isEmpty()) {
            return AiMoveResult(move = null, depth = 0)
        }

        return AiMoveResult(
            move = moves.random(random),
            depth = 0,
        )
    }
}
