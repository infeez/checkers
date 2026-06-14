package com.infeez.simple.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.infeez.simple.base.GameSpriteBatch

abstract class GameObject(
    val id: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    protected val batch: GameSpriteBatch?,
) : Cloneable {
    private val bounds = Rectangle(x, y, width, height)
    private var region: TextureRegion? = null

    val x: Float
        get() = bounds.x

    val y: Float
        get() = bounds.y

    val width: Float
        get() = bounds.width

    val height: Float
        get() = bounds.height

    val rectangle: Rectangle
        get() = bounds

    val textureRegion: TextureRegion?
        get() = region

    fun setX(x: Float) {
        bounds.x = x
    }

    fun setY(y: Float) {
        bounds.y = y
    }

    fun setWidth(width: Float) {
        bounds.width = width
    }

    fun setHeight(height: Float) {
        bounds.height = height
    }

    fun setRectangle(newRectangle: Rectangle) {
        bounds.set(newRectangle)
    }

    fun setTextureRegion(textureRegion: TextureRegion) {
        region = textureRegion
    }

    open fun draw() {
        if (batch == null || region == null) {
            return
        }
        batch.draw(this)
    }

    open fun update() {
    }

    fun contains(gameObject: GameObject): Boolean = contains(gameObject.x, gameObject.y)

    fun contains(x: Float, y: Float): Boolean = bounds.contains(x, y)

    open fun dispose() {
    }

    fun cloneGameObject(): GameObject = clone() as GameObject

    override fun toString(): String = "GameObject{id=$id}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as GameObject
        return id == other.id
    }

    override fun hashCode(): Int = id
}
