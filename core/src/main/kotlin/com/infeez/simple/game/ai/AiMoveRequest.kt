package com.infeez.simple.game.ai

import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.PlayerColor

data class AiMoveRequest(
    val gameState: GameState,
    val aiSide: PlayerColor,
    val difficulty: AiDifficulty,
    val searchLimits: SearchLimits,
)
