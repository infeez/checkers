package com.infeez.simple.game.ai

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor

interface BoardEvaluator {
    fun evaluate(state: GameState, side: PlayerColor): Int
}

class BasicBoardEvaluator : BoardEvaluator {
    override fun evaluate(state: GameState, side: PlayerColor): Int {
        val status = state.status
        if (status is GameStatus.Winner) {
            return if (status.color == side) WIN_SCORE else -WIN_SCORE
        }

        return state.board.pieces.entries.sumOf { (position, piece) ->
            val value = pieceValue(piece, position)
            if (piece.color == side) value else -value
        }
    }

    private fun pieceValue(piece: Piece, position: BoardPosition): Int {
        val material = when (piece.kind) {
            PieceKind.MAN -> MAN_VALUE
            PieceKind.KING -> KING_VALUE
        }
        val advancement = if (piece.kind == PieceKind.MAN) {
            when (piece.color) {
                PlayerColor.WHITE -> (BoardPosition.BOARD_SIZE - 1 - position.row) * ADVANCEMENT_VALUE
                PlayerColor.BLACK -> position.row * ADVANCEMENT_VALUE
            }
        } else {
            0
        }
        val center = if (position.col in CENTER_RANGE && position.row in CENTER_RANGE) CENTER_VALUE else 0
        return material + advancement + center
    }

    private companion object {
        const val WIN_SCORE = 10_000
        const val MAN_VALUE = 100
        const val KING_VALUE = 300
        const val ADVANCEMENT_VALUE = 4
        const val CENTER_VALUE = 10
        val CENTER_RANGE = 2..5
    }
}
