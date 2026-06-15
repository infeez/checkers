package com.infeez.simple.game.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardStateModelTest {
    @Test
    fun place_returnsNewStateWithPieceAndKeepsOriginalEmpty() {
        val position = BoardPosition(0, 7)
        val piece = Piece(PlayerColor.WHITE, PieceKind.MAN)
        val original = BoardState()

        val updated = original.place(position, piece)

        assertNull(original.pieceAt(position))
        assertEquals(piece, updated.pieceAt(position))
        assertTrue(updated.isOccupied(position))
    }

    @Test
    fun remove_returnsNewStateWithoutPieceAndKeepsOriginalOccupied() {
        val position = BoardPosition(0, 7)
        val piece = Piece(PlayerColor.WHITE, PieceKind.MAN)
        val original = BoardState(mapOf(position to piece))

        val updated = original.remove(position)

        assertEquals(piece, original.pieceAt(position))
        assertNull(updated.pieceAt(position))
        assertTrue(updated.isEmpty(position))
    }

    @Test
    fun move_returnsNewStateWithPieceMovedAndKeepsOriginalUnchanged() {
        val from = BoardPosition(0, 7)
        val to = BoardPosition(1, 6)
        val piece = Piece(PlayerColor.WHITE, PieceKind.MAN)
        val original = BoardState(mapOf(from to piece))

        val updated = original.move(from, to)

        assertEquals(piece, original.pieceAt(from))
        assertNull(updated.pieceAt(from))
        assertEquals(piece, updated.pieceAt(to))
        assertFalse(updated.isOccupied(from))
    }
}
