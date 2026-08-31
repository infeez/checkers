package com.infeez.simple.game.ai

import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.CheckersRules

interface MoveOrdering {
    fun orderMoves(state: GameState, side: PlayerColor, moves: List<Move>): List<Move>
}

class DefaultMoveOrdering(
    private val rules: CheckersRules,
) : MoveOrdering {
    override fun orderMoves(state: GameState, side: PlayerColor, moves: List<Move>): List<Move> {
        return moves.sortedByDescending { move -> scoreMove(state, side, move) }
    }

    private fun scoreMove(state: GameState, side: PlayerColor, move: Move): Int {
        val movedPiece = state.board.pieceAt(move.from)
        val capturedPiece = move.captured?.let(state.board::pieceAt)
        val result = rules.applyMove(state, move) as? MoveResult.Success
        val promoted = result?.appliedMove?.let { applied ->
            applied.movedPieceBefore.kind == PieceKind.MAN && applied.movedPieceAfter.kind == PieceKind.KING
        } == true

        val directionBonus = movedPiece?.let { piece ->
            if (piece.kind != PieceKind.MAN || piece.color != side) {
                0
            } else {
                when (side) {
                    PlayerColor.WHITE -> move.from.row - move.to.row
                    PlayerColor.BLACK -> move.to.row - move.from.row
                }
            }
        } ?: 0

        return listOf(
            if (move.type == MoveType.CAPTURE) 10_000 else 0,
            if (capturedPiece?.kind == PieceKind.KING) 1_000 else 0,
            if (promoted) 600 else 0,
            if (movedPiece?.kind == PieceKind.KING) 200 else 0,
            directionBonus * 10,
        ).sum()
    }
}
