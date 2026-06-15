package com.infeez.simple.game.rules

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult

interface CheckersRules {
    fun createInitialState(): GameState

    fun legalMoves(state: GameState): List<Move>

    fun legalMovesForPiece(state: GameState, from: BoardPosition): List<Move>

    fun applyMove(state: GameState, move: Move): MoveResult
}
