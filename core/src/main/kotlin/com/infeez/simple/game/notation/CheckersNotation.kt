package com.infeez.simple.game.notation

import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveType

data class NotationMove(
    val from: BoardPosition,
    val to: BoardPosition,
    val type: MoveType,
)

object CheckersNotation {
    fun parsePosition(value: String): Result<BoardPosition> {
        return runCatching {
            val normalized = value.trim().lowercase()
            require(normalized.length == POSITION_LENGTH) {
                "Position must be exactly 2 characters."
            }

            val file = normalized[0]
            val rank = normalized[1]
            require(file in FIRST_FILE..LAST_FILE) {
                "Position file must be in range a..h."
            }
            require(rank in FIRST_RANK..LAST_RANK) {
                "Position rank must be in range 1..8."
            }

            BoardPosition(
                col = file - FIRST_FILE,
                row = BoardPosition.BOARD_SIZE - rank.digitToInt(),
            )
        }
    }

    fun formatPosition(position: BoardPosition): String {
        require(position.isInsideBoard()) {
            "Position $position must be inside the board."
        }

        val file = (FIRST_FILE.code + position.col).toChar()
        val rank = BoardPosition.BOARD_SIZE - position.row
        return "$file$rank"
    }

    fun parseMove(value: String): Result<NotationMove> {
        return runCatching {
            val normalized = value.trim().lowercase()
            val separator = when {
                SIMPLE_MOVE_SEPARATOR in normalized -> SIMPLE_MOVE_SEPARATOR
                CAPTURE_MOVE_SEPARATOR in normalized -> CAPTURE_MOVE_SEPARATOR
                else -> error("Move must contain '-' or ':'.")
            }

            val parts = normalized.split(separator)
            require(parts.size == MOVE_PART_COUNT) {
                "Move must contain source and target positions."
            }

            NotationMove(
                from = parsePosition(parts[0]).getOrThrow(),
                to = parsePosition(parts[1]).getOrThrow(),
                type = if (separator == CAPTURE_MOVE_SEPARATOR) MoveType.CAPTURE else MoveType.SIMPLE,
            )
        }
    }

    fun formatMove(move: Move): String {
        val separator = when (move.type) {
            MoveType.SIMPLE -> SIMPLE_MOVE_SEPARATOR
            MoveType.CAPTURE -> CAPTURE_MOVE_SEPARATOR
        }
        return "${formatPosition(move.from)}$separator${formatPosition(move.to)}"
    }

    private const val POSITION_LENGTH = 2
    private const val MOVE_PART_COUNT = 2
    private const val FIRST_FILE = 'a'
    private const val LAST_FILE = 'h'
    private const val FIRST_RANK = '1'
    private const val LAST_RANK = '8'
    private const val SIMPLE_MOVE_SEPARATOR = '-'
    private const val CAPTURE_MOVE_SEPARATOR = ':'
}
