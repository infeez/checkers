package com.infeez.simple.game.ai

import com.infeez.simple.game.model.Move

data class AiMoveResult(
    val move: Move?,
    val score: Int? = null,
    val searchedNodes: Int = 0,
    val depth: Int = 0,
)
