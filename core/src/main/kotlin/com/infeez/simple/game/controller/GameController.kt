package com.infeez.simple.game.controller

import com.infeez.simple.game.ai.AiConfigFactory
import com.infeez.simple.game.ai.AiMoveRequest
import com.infeez.simple.game.ai.AiMoveResult
import com.infeez.simple.game.ai.CheckersAi
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.InvalidMoveReason
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.rules.CheckersRules

class GameController(
    private val rules: CheckersRules,
    private val ai: CheckersAi?,
    val config: GameConfig,
) {
    var state: GameState = rules.createInitialState()
        private set

    var turnState: TurnState = turnStateFor(state)
        private set

    fun reset() {
        replaceState(rules.createInitialState())
    }

    fun replaceState(newState: GameState) {
        state = newState
        turnState = turnStateFor(state)
    }

    fun isHumanTurn(): Boolean {
        return turnState == TurnState.HUMAN_TURN
    }

    fun isAiTurn(): Boolean {
        return turnState == TurnState.AI_THINKING
    }

    fun makeHumanMove(move: Move): MoveResult {
        if (state.status != GameStatus.InProgress) {
            return MoveResult.Invalid(InvalidMoveReason.GAME_ALREADY_FINISHED)
        }
        if (!isHumanTurn()) {
            return MoveResult.Invalid(InvalidMoveReason.NOT_CURRENT_PLAYER_PIECE)
        }

        return applyMoveToState(move)
    }

    fun createAiMoveRequest(): AiMoveRequest? {
        if (!isAiTurn()) {
            return null
        }

        return AiMoveRequest(
            gameState = state,
            aiSide = config.aiSide,
            difficulty = config.aiDifficulty,
            searchLimits = AiConfigFactory.create(config.aiDifficulty),
        )
    }

    fun makeAiMove(): AiMoveResult {
        val request = createAiMoveRequest() ?: return AiMoveResult(move = null)
        val result = ai?.chooseMove(request) ?: AiMoveResult(move = null)
        applyAiMoveResult(result)
        return result
    }

    fun applyAiMoveResult(result: AiMoveResult): MoveResult {
        if (state.status != GameStatus.InProgress) {
            return MoveResult.Invalid(InvalidMoveReason.GAME_ALREADY_FINISHED)
        }
        if (!isAiTurn()) {
            return MoveResult.Invalid(InvalidMoveReason.NOT_CURRENT_PLAYER_PIECE)
        }

        val move = result.move ?: return MoveResult.Invalid(InvalidMoveReason.UNKNOWN)
        return applyMoveToState(move)
    }

    private fun applyMoveToState(move: Move): MoveResult {
        val result = rules.applyMove(state, move)
        if (result is MoveResult.Success) {
            replaceState(result.state)
        }
        return result
    }

    private fun turnStateFor(state: GameState): TurnState {
        if (state.status != GameStatus.InProgress) {
            return TurnState.GAME_OVER
        }

        return when (config.gameMode) {
            GameMode.HUMAN_VS_HUMAN -> TurnState.HUMAN_TURN
            GameMode.HUMAN_VS_AI -> if (state.currentTurn == config.humanSide) {
                TurnState.HUMAN_TURN
            } else {
                TurnState.AI_THINKING
            }
        }
    }
}
