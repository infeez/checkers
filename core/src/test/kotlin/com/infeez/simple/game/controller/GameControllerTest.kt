package com.infeez.simple.game.controller

import com.infeez.simple.game.ai.AiDifficulty
import com.infeez.simple.game.ai.AiMoveRequest
import com.infeez.simple.game.ai.AiMoveResult
import com.infeez.simple.game.ai.CheckersAi
import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.InvalidMoveReason
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.RussianCheckersRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameControllerTest {
    private val rules = RussianCheckersRules()

    @Test
    fun humanVsAi_afterHumanMove_entersAiThinking() {
        val controller = controller()

        val result = controller.makeHumanMove(simple(BoardPosition(0, 5), BoardPosition(1, 4)))

        assertTrue(result is MoveResult.Success)
        assertEquals(TurnState.AI_THINKING, controller.turnState)
    }

    @Test
    fun humanVsAi_rejectsHumanMoveDuringAiThinking() {
        val controller = controller()
        controller.makeHumanMove(simple(BoardPosition(0, 5), BoardPosition(1, 4)))

        val result = controller.makeHumanMove(simple(BoardPosition(1, 2), BoardPosition(0, 3)))

        assertEquals(MoveResult.Invalid(InvalidMoveReason.NOT_CURRENT_PLAYER_PIECE), result)
    }

    @Test
    fun humanVsHuman_doesNotEnterAiThinking() {
        val controller = controller(
            config = GameConfig(
                gameMode = GameMode.HUMAN_VS_HUMAN,
                humanSide = PlayerColor.WHITE,
                aiDifficulty = AiDifficulty.RANDOM,
            ),
        )

        controller.makeHumanMove(simple(BoardPosition(0, 5), BoardPosition(1, 4)))

        assertEquals(TurnState.HUMAN_TURN, controller.turnState)
    }

    @Test
    fun humanBlack_aiHasFirstTurn() {
        val controller = controller(
            ai = FirstLegalMoveAi(rules),
            config = GameConfig(
                gameMode = GameMode.HUMAN_VS_AI,
                humanSide = PlayerColor.BLACK,
                aiDifficulty = AiDifficulty.RANDOM,
            ),
        )

        assertEquals(TurnState.AI_THINKING, controller.turnState)

        val result = controller.makeAiMove()

        assertTrue(result.move != null)
        assertEquals(PlayerColor.BLACK, controller.state.currentTurn)
        assertEquals(TurnState.HUMAN_TURN, controller.turnState)
    }

    @Test
    fun illegalHumanMove_isRejected() {
        val controller = controller()

        val result = controller.makeHumanMove(simple(BoardPosition(0, 5), BoardPosition(0, 4)))

        assertTrue(result is MoveResult.Invalid)
    }

    @Test
    fun illegalAiMove_isRejected() {
        val controller = controller(
            ai = FixedMoveAi(simple(BoardPosition(0, 5), BoardPosition(0, 4))),
            config = GameConfig(
                gameMode = GameMode.HUMAN_VS_AI,
                humanSide = PlayerColor.BLACK,
                aiDifficulty = AiDifficulty.RANDOM,
            ),
        )

        val result = controller.makeAiMove()

        assertTrue(result.move != null)
        assertEquals(PlayerColor.WHITE, controller.state.currentTurn)
        assertEquals(TurnState.AI_THINKING, controller.turnState)
    }

    private fun controller(
        ai: CheckersAi = FirstLegalMoveAi(rules),
        config: GameConfig = GameConfig(
            gameMode = GameMode.HUMAN_VS_AI,
            humanSide = PlayerColor.WHITE,
            aiDifficulty = AiDifficulty.RANDOM,
        ),
    ): GameController {
        return GameController(rules, ai, config)
    }

    private fun simple(from: BoardPosition, to: BoardPosition): Move {
        return Move(from = from, to = to, type = MoveType.SIMPLE)
    }

    private class FirstLegalMoveAi(
        private val rules: RussianCheckersRules,
    ) : CheckersAi {
        override fun chooseMove(request: AiMoveRequest): AiMoveResult {
            return AiMoveResult(move = rules.legalMoves(request.gameState).firstOrNull())
        }
    }

    private class FixedMoveAi(
        private val move: Move,
    ) : CheckersAi {
        override fun chooseMove(request: AiMoveRequest): AiMoveResult {
            return AiMoveResult(move = move)
        }
    }
}
