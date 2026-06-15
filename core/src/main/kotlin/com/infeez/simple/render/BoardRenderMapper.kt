package com.infeez.simple.render

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor

data class RenderablePiece(
    val position: BoardPosition,
    val color: PlayerColor,
    val kind: PieceKind,
)

class BoardRenderMapper {
    fun toRenderablePieces(state: GameState): List<RenderablePiece> {
        return state.board.pieces.map { (position, piece) ->
            RenderablePiece(
                position = position,
                color = piece.color,
                kind = piece.kind,
            )
        }
    }
}
