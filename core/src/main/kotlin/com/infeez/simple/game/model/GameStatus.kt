package com.infeez.simple.game.model

sealed interface GameStatus {
    data object InProgress : GameStatus

    data class Winner(
        val color: PlayerColor,
        val reason: WinReason,
    ) : GameStatus

    data class Draw(
        val reason: DrawReason,
    ) : GameStatus
}

enum class WinReason {
    OPPONENT_HAS_NO_PIECES,
    OPPONENT_HAS_NO_LEGAL_MOVES,
}

enum class DrawReason {
    MANUAL,
}
