package com.infeez.simple.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.infeez.simple.ResourceSingleton
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardCommandUtil
import com.infeez.simple.utils.BoardConfig
import com.infeez.simple.utils.Constants.GameEnvTypes

class Cell(
    private val arrayPosI: Int,
    private val arrayPosJ: Int,
    x: Float,
    y: Float,
    private val type: GameEnvTypes,
    batch: GameSpriteBatch?,
) : GameObject(
    ResourceSingleton.getUniqueId(),
    x,
    y,
    BoardConfig.CELL_SIZE,
    BoardConfig.CELL_SIZE,
    batch,
) {
    var checker: Checker? = null
        private set

    private var capturedChecker: Checker? = null

    init {
        textureRegionFor(type)?.let(::setTextureRegion)
    }

    val boardStringPosition: String
        get() = BoardCommandUtil.checkerPositionToCommand(boardPosition)

    val boardPosition: BoardArrayPosition
        get() = BoardArrayPosition(arrayPosI, arrayPosJ)

    fun isBlackType(): Boolean = type == GameEnvTypes.BLACK

    fun isWhiteType(): Boolean = type == GameEnvTypes.WHITE

    fun isChecker(): Boolean = checker != null

    fun setChecker(type: GameEnvTypes): Checker {
        checker = Checker(boardPosition, x, y, type, batch)
        return checker ?: error("Checker was not created.")
    }

    fun removeChecker(): GameEnvTypes? {
        capturedChecker = null
        val currentChecker = checker ?: return null
        checker = null
        return currentChecker.type
    }

    fun drawChecker() {
        val currentChecker = checker ?: return
        if (currentChecker === capturedChecker) {
            return
        }
        batch?.draw(currentChecker)
    }

    fun drawCapturedChecker() {
        val currentCapturedChecker = capturedChecker ?: return
        batch?.draw(currentCapturedChecker)
    }

    fun captureChecker(x: Float, y: Float) {
        val currentChecker = checker ?: return
        currentChecker.setX(x - BoardConfig.CELL_SIZE / 2f)
        currentChecker.setY(y - BoardConfig.CELL_SIZE / 2f)
        capturedChecker = currentChecker
    }

    fun cancelCapture() {
        val currentCapturedChecker = capturedChecker ?: return
        currentCapturedChecker.setX(x)
        currentCapturedChecker.setY(y)
        capturedChecker = null
    }

    private fun textureRegionFor(type: GameEnvTypes): TextureRegion? {
        if (Gdx.files == null) {
            return null
        }
        return when (type) {
            GameEnvTypes.WHITE -> ResourceSingleton.getWhiteCell()
            GameEnvTypes.BLACK -> ResourceSingleton.getBlackCell()
        }
    }

    override fun toString(): String {
        return "Cell{type=$type, checker=$checker}\nfrom ${super.toString()}"
    }
}
