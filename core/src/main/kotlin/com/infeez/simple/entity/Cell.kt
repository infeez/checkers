package com.infeez.simple.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.infeez.simple.ResourceSingleton
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.game.model.PieceKind
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

    val boardPosition: BoardArrayPosition
        get() = BoardArrayPosition(arrayPosI, arrayPosJ)

    fun isBlackType(): Boolean = type == GameEnvTypes.BLACK

    fun isChecker(): Boolean = checker != null

    fun setChecker(type: GameEnvTypes, kind: PieceKind = PieceKind.MAN): Checker {
        checker = Checker(x, y, type, kind, batch)
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

    fun drawHighlight() {
        val currentBatch = batch ?: return
        if (Gdx.files == null) {
            return
        }

        val oldColor = Color(currentBatch.color)
        currentBatch.setColor(0.15f, 0.75f, 0.35f, 0.55f)
        currentBatch.draw(ResourceSingleton.getWhiteCell(), x, y, width, height)
        currentBatch.setColor(oldColor)
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
