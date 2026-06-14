package com.infeez.simple.entity

import com.badlogic.gdx.Gdx
import com.infeez.simple.Cells
import com.infeez.simple.ResourceSingleton
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.input.PCInputProcessor
import com.infeez.simple.state.CheckerColor
import com.infeez.simple.state.CheckerState
import com.infeez.simple.state.GameState
import com.infeez.simple.state.toCheckerColor
import com.infeez.simple.state.toGameEnvType
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardCommandUtil
import com.infeez.simple.utils.BoardConfig
import com.infeez.simple.utils.Constants.GameEnvTypes

class Board(spriteBatch: GameSpriteBatch? = null) : GameObject(
    ResourceSingleton.getUniqueId(),
    0f,
    0f,
    graphicsWidth(),
    graphicsHeight(),
    spriteBatch,
), PCInputProcessor {
    internal val cells = Cells()
    private var dragged = false
    private var cellForDrag: Cell? = null
    private var activePointerId: Int? = null
    private var currentTurn: CheckerColor? = CheckerColor.WHITE
    private var moveNumber = 0

    fun create() {
        cells.createBoard(batch)
    }

    override fun draw() {
        for (cell in cells) {
            cell.draw()
        }
        for (cell in cells) {
            cell.drawChecker()
        }
        for (cell in cells) {
            cell.drawCapturedChecker()
        }
    }

    override fun update() {
        for (cell in cells) {
            cell.update()
        }
    }

    override fun dispose() {
        for (cell in cells) {
            cell.dispose()
        }
    }

    override fun mouseDown(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != null) {
            return false
        }

        val selectedCell = cells.findCellByCoordinatesAndHaveChecker(x, y) ?: return false
        activePointerId = pointer
        cellForDrag = selectedCell
        dragged = false
        return true
    }

    override fun mouseDrag(x: Float, y: Float, pointer: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }

        val dragCell = cellForDrag
        if (dragCell != null) {
            dragCell.captureChecker(x, y)
            dragged = true
            return true
        } else {
            cellForDrag = cells.findCellByCoordinatesAndHaveChecker(x, y)
        }

        return cellForDrag != null
    }

    override fun mouseUp(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }

        val sourceCell = cellForDrag
        if (!dragged || sourceCell == null) {
            cancelActiveDrag()
            return false
        }

        val type = sourceCell.removeChecker()
        if (type == null) {
            resetDrag()
            return false
        }

        val targetCell = cells.findCellByCoordinates(x, y)
        val newChecker = if (targetCell != null && !targetCell.isChecker() && targetCell.isBlackType()) {
            targetCell.setChecker(type)
        } else {
            sourceCell.setChecker(type)
            null
        }

        if (newChecker != null) {
            moveNumber++
            currentTurn = currentTurn?.opposite()
            println("From ${sourceCell.boardStringPosition} to ${newChecker.boardStringPosition}")
        }
        resetDrag()
        return newChecker != null
    }

    override fun touchCancelled(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }

        cancelActiveDrag()
        return true
    }

    fun moveChecker(from: String, to: String) {
        val chPosFrom = BoardCommandUtil.parseCommand(from)
        val chPosTo = BoardCommandUtil.parseCommand(to)

        val sourceCell = cells.getCell(chPosFrom)
        val checkerToMove = sourceCell.checker ?: return
        val targetCell = cells.getCell(chPosTo)
        if (targetCell.isChecker() || !targetCell.isBlackType()) {
            return
        }

        sourceCell.removeChecker()
        targetCell.setChecker(checkerToMove.type)
        moveNumber++
        currentTurn = currentTurn?.opposite()
    }

    fun animateMoveChecker(from: String, to: String) {
        animateMoveChecker(BoardCommandUtil.parseCommand(from), BoardCommandUtil.parseCommand(to))
    }

    fun animateMoveChecker(from: BoardArrayPosition, to: BoardArrayPosition) {
    }

    fun startNewGame() {
        cells.startCellsPosition()
        currentTurn = CheckerColor.WHITE
        moveNumber = 0
    }

    fun toGameState(): GameState {
        val checkerStates = cells
            .mapNotNull { cell ->
                val checker = cell.checker ?: return@mapNotNull null
                val command = BoardCommandUtil.checkerPositionToCommand(cell.boardPosition)
                CheckerState(
                    id = "${checker.type.name.lowercase()}-$command",
                    color = checker.type.toCheckerColor(),
                    position = BoardArrayPosition(
                        cell.boardPosition.indexFirst,
                        cell.boardPosition.indexSecond,
                    ),
                )
            }

        return GameState(
            board = checkerStates,
            currentTurn = currentTurn,
            moveNumber = moveNumber,
        )
    }

    fun tryRestoreGameState(state: GameState): Boolean {
        return runCatching {
            restoreGameState(state)
        }.isSuccess
    }

    fun cancelActiveDrag() {
        cellForDrag?.cancelCapture()
        resetDrag()
    }

    fun checkWinner(): Winner {
        var whiteCount = 0
        var blackCount = 0
        for (cell in cells) {
            when {
                cell.checker?.isBlackType() == true -> blackCount++
                cell.checker?.isWhiteType() == true -> whiteCount++
            }
        }

        return when {
            whiteCount == 0 && blackCount > 0 -> Winner.BLACK
            blackCount == 0 && whiteCount > 0 -> Winner.WHITE
            else -> Winner.NONE
        }
    }

    internal fun getCell(command: String): Cell = cells.getCell(BoardCommandUtil.parseCommand(command))

    private fun resetDrag() {
        activePointerId = null
        cellForDrag = null
        dragged = false
    }

    private fun restoreGameState(state: GameState) {
        val occupiedPositions = HashSet<BoardArrayPosition>()
        for (checkerState in state.board) {
            require(occupiedPositions.add(checkerState.position)) {
                "Duplicate checker position ${checkerState.position}."
            }
            require(cells.getCell(checkerState.position).isBlackType()) {
                "Checker must be placed on a black cell."
            }
        }

        cells.clearCheckers()
        for (checkerState in state.board) {
            cells.getCell(checkerState.position).setChecker(checkerState.color.toGameEnvType())
        }
        currentTurn = state.currentTurn
        moveNumber = state.moveNumber
    }

    private fun CheckerColor.opposite(): CheckerColor {
        return when (this) {
            CheckerColor.WHITE -> CheckerColor.BLACK
            CheckerColor.BLACK -> CheckerColor.WHITE
        }
    }

    companion object {
        private fun graphicsWidth(): Float {
            return (Gdx.graphics?.width ?: BoardConfig.BOARD_PIXEL_SIZE).toFloat()
        }

        private fun graphicsHeight(): Float {
            return (Gdx.graphics?.height ?: BoardConfig.BOARD_PIXEL_SIZE).toFloat()
        }
    }
}
