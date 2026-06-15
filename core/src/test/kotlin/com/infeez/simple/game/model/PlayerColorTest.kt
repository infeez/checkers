package com.infeez.simple.game.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerColorTest {
    @Test
    fun opponent_white_returnsBlack() {
        assertEquals(PlayerColor.BLACK, PlayerColor.WHITE.opponent())
    }

    @Test
    fun opponent_black_returnsWhite() {
        assertEquals(PlayerColor.WHITE, PlayerColor.BLACK.opponent())
    }
}
