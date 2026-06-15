package com.infeez.simple.game.model

enum class MoveType {
    SIMPLE,
    CAPTURE,
}

data class Move(
    val from: BoardPosition,
    val to: BoardPosition,
    val type: MoveType,
    val captured: BoardPosition? = null,
)

data class AppliedMove(
    val move: Move,
    val movedPieceBefore: Piece,
    val movedPieceAfter: Piece,
    val turnBefore: PlayerColor,
    val turnAfter: PlayerColor,
    val statusAfter: GameStatus,
)
