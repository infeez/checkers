package com.infeez.simple.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.infeez.simple.ResourceSingleton
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardCommandUtil
import com.infeez.simple.utils.BoardConfig
import com.infeez.simple.utils.Constants.GameEnvTypes

class Checker(
    x: Float,
    y: Float,
    val type: GameEnvTypes,
    val kind: PieceKind,
    batch: GameSpriteBatch?,
) : GameObject(
    ResourceSingleton.getUniqueId(),
    x,
    y,
    BoardConfig.CELL_SIZE,
    BoardConfig.CELL_SIZE,
    batch,
) {
    init {
        textureRegionFor(type)?.let(::setTextureRegion)
    }

    private fun textureRegionFor(type: GameEnvTypes): TextureRegion? {
        if (Gdx.files == null) {
            return null
        }
        return when (type) {
            GameEnvTypes.WHITE -> ResourceSingleton.getWhiteChecker()
            GameEnvTypes.BLACK -> ResourceSingleton.getBlackChecker()
        }
    }
}
