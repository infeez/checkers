package com.infeez.simple.game.ai

import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.RussianCheckersRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RandomAiTest {
    private val rules = RussianCheckersRules()

    @Test
    fun chooseMove_returnsLegalMove() {
        val state = rules.createInitialState()
        val ai = RandomAi(rules, Random(0))

        val result = ai.chooseMove(request(state, PlayerColor.WHITE))

        assertTrue(result.move in rules.legalMoves(state))
        assertEquals(0, result.depth)
    }

    @Test
    fun chooseMove_withoutLegalMoves_returnsNull() {
        val state = GameState(
            board = BoardState(),
            currentTurn = PlayerColor.WHITE,
        )
        val ai = RandomAi(rules, Random(0))

        assertNull(ai.chooseMove(request(state, PlayerColor.WHITE)).move)
    }

    @Test
    fun chooseMove_forWrongSide_returnsNull() {
        val state = rules.createInitialState()
        val ai = RandomAi(rules, Random(0))

        assertNull(ai.chooseMove(request(state, PlayerColor.BLACK)).move)
    }

    @Test
    fun chooseMove_doesNotMutateState() {
        val state = rules.createInitialState()
        val ai = RandomAi(rules, Random(0))

        ai.chooseMove(request(state, PlayerColor.WHITE))

        assertEquals(state, rules.createInitialState())
    }

    private fun request(state: GameState, side: PlayerColor): AiMoveRequest {
        return AiMoveRequest(
            gameState = state,
            aiSide = side,
            difficulty = AiDifficulty.RANDOM,
            searchLimits = SearchLimits(maxDepth = 0, randomizeEqualMoves = false),
        )
    }
}
