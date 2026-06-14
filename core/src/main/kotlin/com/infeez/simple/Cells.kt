package com.infeez.simple

import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.entity.Cell
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardConfig
import com.infeez.simple.utils.Constants.GameEnvTypes

class Cells : Iterable<Cell> {
    private val cells: Array<Array<Cell?>> = Array(BoardConfig.BOARD_SIZE) {
        arrayOfNulls(BoardConfig.BOARD_SIZE)
    }

    fun createBoard(batch: GameSpriteBatch? = null) {
        var y = 0f
        for (row in 0 until BoardConfig.BOARD_SIZE) {
            var x = 0f
            for (column in 0 until BoardConfig.BOARD_SIZE) {
                val type = if ((column + row) % 2 == 0) {
                    GameEnvTypes.WHITE
                } else {
                    GameEnvTypes.BLACK
                }
                val cell = Cell(column, row, x, y, type, batch)
                setCell(cell, column, row)
                x += BoardConfig.CELL_SIZE
            }
            y += BoardConfig.CELL_SIZE
        }
    }

    fun startCellsPosition() {
        clearCheckers()
        for (cell in this) {
            val row = cell.boardPosition.indexSecond
            if (row <= 2 && cell.isBlackType()) {
                cell.setChecker(GameEnvTypes.BLACK)
            } else if (row >= 5 && cell.isBlackType()) {
                cell.setChecker(GameEnvTypes.WHITE)
            }
        }
    }

    fun getCell(boardArrayPosition: BoardArrayPosition): Cell {
        require(boardArrayPosition.indexFirst in 0 until BoardConfig.BOARD_SIZE) {
            "Board file index must be in range 0..7."
        }
        require(boardArrayPosition.indexSecond in 0 until BoardConfig.BOARD_SIZE) {
            "Board rank index must be in range 0..7."
        }
        return cells[boardArrayPosition.indexFirst][boardArrayPosition.indexSecond]
            ?: error("Board has not been created.")
    }

    fun setCell(cell: Cell, i: Int, j: Int) {
        cells[i][j] = cell
    }

    fun clearCheckers(type: GameEnvTypes? = null) {
        for (cell in this) {
            if (type == null || cell.checker?.type == type) {
                cell.removeChecker()
            }
        }
    }

    fun findCellByCoordinates(x: Float, y: Float): Cell? {
        return firstOrNull { cell ->
            cell.contains(x, y)
        }
    }

    fun findCellByCoordinatesAndHaveChecker(x: Float, y: Float): Cell? {
        return firstOrNull { cell ->
            cell.contains(x, y) && cell.isChecker()
        }
    }

    fun toList(): List<Cell> {
        val result = ArrayList<Cell>(BoardConfig.BOARD_SIZE * BoardConfig.BOARD_SIZE)
        for (column in cells) {
            for (cell in column) {
                if (cell != null) {
                    result.add(cell)
                }
            }
        }
        return result
    }

    override fun iterator(): Iterator<Cell> = toList().iterator()
}
