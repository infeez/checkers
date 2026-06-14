package com.infeez.simple.state

import com.infeez.simple.utils.BoardArrayPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateSerializerTest {
    @Test
    fun roundTrip_validState_restoresSameData() {
        val state = GameState(
            board = listOf(
                CheckerState(
                    id = "white-a1",
                    color = CheckerColor.WHITE,
                    position = BoardArrayPosition(0, 7),
                ),
                CheckerState(
                    id = "black-b6",
                    color = CheckerColor.BLACK,
                    position = BoardArrayPosition(1, 2),
                    isKing = true,
                ),
            ),
            currentTurn = CheckerColor.BLACK,
            moveNumber = 3,
        )

        val restored = GameStateSerializer.deserialize(GameStateSerializer.serialize(state))

        assertEquals(state, restored)
    }

    @Test
    fun deserialize_corruptedState_returnsNull() {
        assertNull(GameStateSerializer.deserialize("not a valid state"))
        assertNull(GameStateSerializer.deserialize(null))
        assertNull(GameStateSerializer.deserialize(""))
    }
}
