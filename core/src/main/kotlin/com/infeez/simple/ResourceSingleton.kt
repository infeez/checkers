package com.infeez.simple

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.infeez.simple.utils.BoardConfig
import java.util.concurrent.atomic.AtomicInteger

object ResourceSingleton {
    private var cellsTexture: Texture? = null
    private var blackCell: TextureRegion? = null
    private var whiteCell: TextureRegion? = null
    private var checkersTexture: Texture? = null
    private var blackChecker: TextureRegion? = null
    private var whiteChecker: TextureRegion? = null

    private val nextId = AtomicInteger()

    @JvmStatic
    fun getCellsTexture(): Texture {
        return cellsTexture ?: Texture(Gdx.files.internal("cells.jpg")).also {
            cellsTexture = it
        }
    }

    @JvmStatic
    fun getCheckersTexture(): Texture {
        return checkersTexture ?: Texture(Gdx.files.internal("checkers.png")).also {
            checkersTexture = it
        }
    }

    @JvmStatic
    fun getBlackCell(): TextureRegion {
        return blackCell ?: TextureRegion(
            getCellsTexture(),
            0,
            0,
            BoardConfig.TEXTURE_REGION_SIZE,
            BoardConfig.TEXTURE_REGION_SIZE,
        ).also {
            blackCell = it
        }
    }

    @JvmStatic
    fun getWhiteCell(): TextureRegion {
        return whiteCell ?: TextureRegion(
            getCellsTexture(),
            BoardConfig.TEXTURE_REGION_SIZE,
            0,
            BoardConfig.TEXTURE_REGION_SIZE,
            BoardConfig.TEXTURE_REGION_SIZE,
        ).also {
            whiteCell = it
        }
    }

    @JvmStatic
    fun getBlackChecker(): TextureRegion {
        return blackChecker ?: TextureRegion(
            getCheckersTexture(),
            BoardConfig.TEXTURE_REGION_SIZE,
            0,
            BoardConfig.TEXTURE_REGION_SIZE,
            BoardConfig.TEXTURE_REGION_SIZE,
        ).also {
            blackChecker = it
        }
    }

    @JvmStatic
    fun getWhiteChecker(): TextureRegion {
        return whiteChecker ?: TextureRegion(
            getCheckersTexture(),
            0,
            0,
            BoardConfig.TEXTURE_REGION_SIZE,
            BoardConfig.TEXTURE_REGION_SIZE,
        ).also {
            whiteChecker = it
        }
    }

    @JvmStatic
    fun getUniqueId(): Int {
        return nextId.getAndIncrement()
    }

    @JvmStatic
    fun dispose() {
        cellsTexture?.dispose()
        checkersTexture?.dispose()

        cellsTexture = null
        checkersTexture = null
        blackCell = null
        whiteCell = null
        blackChecker = null
        whiteChecker = null
    }
}
