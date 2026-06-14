package com.infeez.simple.base

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.infeez.simple.entity.GameObject

class GameSpriteBatch : SpriteBatch() {
    fun draw(gameObject: GameObject) {
        val textureRegion = gameObject.textureRegion ?: return
        draw(textureRegion, gameObject.x, gameObject.y, gameObject.width, gameObject.height)
    }
}
