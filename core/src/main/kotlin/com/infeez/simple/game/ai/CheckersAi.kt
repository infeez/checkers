package com.infeez.simple.game.ai

interface CheckersAi {
    fun chooseMove(request: AiMoveRequest): AiMoveResult
}
