package com.infeez.simple.game.engine

import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.WinReason
import com.infeez.simple.game.rules.CheckersRules

class GameEndEvaluator(
    private val rules: CheckersRules,
) {
    fun evaluate(state: GameState): GameStatus {
        if (state.status != GameStatus.InProgress) {
            return state.status
        }

        val sideToMove = state.currentTurn
        if (state.board.positionsOf(sideToMove).isEmpty()) {
            return GameStatus.Winner(
                color = sideToMove.opponent(),
                reason = WinReason.OPPONENT_HAS_NO_PIECES,
            )
        }

        if (rules.legalMoves(state).isEmpty()) {
            return GameStatus.Winner(
                color = sideToMove.opponent(),
                reason = WinReason.OPPONENT_HAS_NO_LEGAL_MOVES,
            )
        }

        return GameStatus.InProgress
    }
}
