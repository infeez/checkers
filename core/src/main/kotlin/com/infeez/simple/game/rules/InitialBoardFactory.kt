package com.infeez.simple.game.rules

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor

object InitialBoardFactory {
    fun russianCheckers(): BoardState {
        val pieces = mutableMapOf<BoardPosition, Piece>()

        for (row in 0 until BoardPosition.BOARD_SIZE) {
            for (col in 0 until BoardPosition.BOARD_SIZE) {
                val position = BoardPosition(col, row)
                if (!position.isDarkSquare()) {
                    continue
                }

                when (row) {
                    in BLACK_INITIAL_ROWS -> pieces[position] = Piece(PlayerColor.BLACK, PieceKind.MAN)
                    in WHITE_INITIAL_ROWS -> pieces[position] = Piece(PlayerColor.WHITE, PieceKind.MAN)
                }
            }
        }

        return BoardState(pieces)
    }

    private val BLACK_INITIAL_ROWS = 0..2
    private val WHITE_INITIAL_ROWS = 5..7
}
