package com.infeez.simple.state

import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.Constants.GameEnvTypes

data class GameState(
    val board: List<CheckerState>,
    val currentTurn: CheckerColor?,
    val moveNumber: Int,
)

data class CheckerState(
    val id: String,
    val color: CheckerColor,
    val position: BoardArrayPosition,
    val isKing: Boolean = false,
)

enum class CheckerColor {
    WHITE,
    BLACK,
}

fun GameEnvTypes.toCheckerColor(): CheckerColor {
    return when (this) {
        GameEnvTypes.WHITE -> CheckerColor.WHITE
        GameEnvTypes.BLACK -> CheckerColor.BLACK
    }
}

fun CheckerColor.toGameEnvType(): GameEnvTypes {
    return when (this) {
        CheckerColor.WHITE -> GameEnvTypes.WHITE
        CheckerColor.BLACK -> GameEnvTypes.BLACK
    }
}
