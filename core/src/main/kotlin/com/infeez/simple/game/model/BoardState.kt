package com.infeez.simple.game.model

data class BoardState(
    val pieces: Map<BoardPosition, Piece> = emptyMap(),
) {
    init {
        require(pieces.keys.all { it.isInsideBoard() }) {
            "All pieces must be placed inside the board."
        }
        require(pieces.keys.all { it.isDarkSquare() }) {
            "All pieces must be placed on dark squares."
        }
    }

    fun pieceAt(position: BoardPosition): Piece? {
        return pieces[position]
    }

    fun isEmpty(position: BoardPosition): Boolean {
        return pieceAt(position) == null
    }

    fun isOccupied(position: BoardPosition): Boolean {
        return pieceAt(position) != null
    }

    fun positionsOf(color: PlayerColor): List<BoardPosition> {
        return pieces
            .filterValues { piece -> piece.color == color }
            .keys
            .toList()
    }

    fun piecesOf(color: PlayerColor): Map<BoardPosition, Piece> {
        return pieces.filterValues { piece -> piece.color == color }
    }

    fun place(position: BoardPosition, piece: Piece): BoardState {
        requireValidPiecePosition(position)
        require(isEmpty(position)) {
            "Position $position is already occupied."
        }
        return copy(pieces = pieces + (position to piece))
    }

    fun remove(position: BoardPosition): BoardState {
        requireValidBoardPosition(position)
        return copy(pieces = pieces - position)
    }

    fun move(from: BoardPosition, to: BoardPosition): BoardState {
        requireValidBoardPosition(from)
        requireValidPiecePosition(to)

        val piece = pieceAt(from)
        require(piece != null) {
            "Source position $from is empty."
        }
        require(isEmpty(to)) {
            "Target position $to is already occupied."
        }

        return copy(pieces = pieces - from + (to to piece))
    }

    fun promote(position: BoardPosition): BoardState {
        requireValidBoardPosition(position)

        val piece = pieceAt(position)
        require(piece != null) {
            "Position $position is empty."
        }

        return copy(pieces = pieces + (position to piece.copy(kind = PieceKind.KING)))
    }

    private fun requireValidPiecePosition(position: BoardPosition) {
        requireValidBoardPosition(position)
        require(position.isDarkSquare()) {
            "Piece position $position must be a dark square."
        }
    }

    private fun requireValidBoardPosition(position: BoardPosition) {
        require(position.isInsideBoard()) {
            "Position $position must be inside the board."
        }
    }
}
