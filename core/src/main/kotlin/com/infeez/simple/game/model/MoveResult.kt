package com.infeez.simple.game.model

sealed interface MoveResult {
    data class Success(
        val state: GameState,
        val appliedMove: AppliedMove,
    ) : MoveResult

    data class Invalid(
        val reason: InvalidMoveReason,
    ) : MoveResult
}

enum class InvalidMoveReason {
    GAME_ALREADY_FINISHED,
    SOURCE_OUT_OF_BOARD,
    TARGET_OUT_OF_BOARD,
    SOURCE_EMPTY,
    NOT_CURRENT_PLAYER_PIECE,
    TARGET_NOT_EMPTY,
    TARGET_NOT_DARK_SQUARE,
    FORCED_CAPTURE_AVAILABLE,
    MUST_CONTINUE_CAPTURE_WITH_SAME_PIECE,
    ILLEGAL_MAN_MOVE,
    ILLEGAL_KING_MOVE,
    NO_OPPONENT_TO_CAPTURE,
    MULTIPLE_PIECES_ON_CAPTURE_PATH,
    OWN_PIECE_ON_CAPTURE_PATH,
    UNKNOWN,
}
