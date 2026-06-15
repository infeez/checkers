package com.infeez.simple.game.model

data class BoardPosition(
    val col: Int,
    val row: Int,
) {
    fun isInsideBoard(): Boolean {
        return col in 0 until BOARD_SIZE && row in 0 until BOARD_SIZE
    }

    fun isDarkSquare(): Boolean {
        return isInsideBoard() && (col + row) % 2 != 0
    }

    fun offset(dc: Int, dr: Int): BoardPosition {
        return BoardPosition(col + dc, row + dr)
    }

    companion object {
        const val BOARD_SIZE = 8
    }
}
