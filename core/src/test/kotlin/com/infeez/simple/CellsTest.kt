package com.infeez.simple

import com.infeez.simple.utils.Constants.GameEnvTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CellsTest {
    @Test
    fun startCellsPosition_createsSixtyFourCells() {
        val cells = createStartedCells()

        assertEquals(64, cells.toList().size)
    }

    @Test
    fun startCellsPosition_placesTwelveBlackCheckers() {
        val cells = createStartedCells()

        assertEquals(12, cells.count { it.checker?.type == GameEnvTypes.BLACK })
    }

    @Test
    fun startCellsPosition_placesTwelveWhiteCheckers() {
        val cells = createStartedCells()

        assertEquals(12, cells.count { it.checker?.type == GameEnvTypes.WHITE })
    }

    @Test
    fun startCellsPosition_placesCheckersOnlyOnBlackCells() {
        val cells = createStartedCells()

        assertTrue(cells.filter { it.checker != null }.all { it.isBlackType() })
    }

    @Test
    fun startCellsPosition_middleTwoRowsAreEmpty() {
        val cells = createStartedCells()

        cells.filter { it.boardPosition.indexSecond in 3..4 }
            .forEach { assertNull(it.checker) }
    }

    private fun createStartedCells(): Cells {
        return Cells().apply {
            createBoard()
            startCellsPosition()
        }
    }
}
