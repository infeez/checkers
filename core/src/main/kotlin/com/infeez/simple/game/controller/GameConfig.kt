package com.infeez.simple.game.controller

import com.infeez.simple.game.ai.AiDifficulty
import com.infeez.simple.game.model.PlayerColor

data class GameConfig(
    val gameMode: GameMode = GameMode.HUMAN_VS_AI,
    val humanSide: PlayerColor = PlayerColor.WHITE,
    val aiDifficulty: AiDifficulty = AiDifficulty.NORMAL,
    val aiDebugEnabled: Boolean = true,
) {
    val aiSide: PlayerColor
        get() = humanSide.opponent()
}

object DefaultGameConfig {
    // Change AI difficulty and human side here while there is no settings UI.
    val value = GameConfig(
        gameMode = GameMode.HUMAN_VS_AI,
        humanSide = PlayerColor.WHITE,
        aiDifficulty = AiDifficulty.EXPERT,
    )
}
