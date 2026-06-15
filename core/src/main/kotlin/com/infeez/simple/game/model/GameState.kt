package com.infeez.simple.game.model

data class GameState(
    val board: BoardState,
    val currentTurn: PlayerColor,
    val status: GameStatus = GameStatus.InProgress,
    val forcedPiece: BoardPosition? = null,
    val moveHistory: List<AppliedMove> = emptyList(),
)
