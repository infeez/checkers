package com.infeez.simple.base

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.infeez.simple.input.PCInputProcessor

abstract class CheckerApplication : Game(), InputProcessor {
    private var pcInputProcessor: PCInputProcessor? = null

    override fun create() {
        Gdx.input.inputProcessor = this
    }

    override fun render() {
        super.render()
    }

    override fun keyDown(keycode: Int): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

    override fun keyTyped(character: Char): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return pcInputProcessor?.mouseDown(screenX.toFloat(), screenY.toFloat(), pointer, button) ?: false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return pcInputProcessor?.mouseUp(screenX.toFloat(), screenY.toFloat(), pointer, button) ?: false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return pcInputProcessor?.touchCancelled(screenX.toFloat(), screenY.toFloat(), pointer, button) ?: false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return pcInputProcessor?.mouseDrag(screenX.toFloat(), screenY.toFloat(), pointer) ?: false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(amountX: Float, amountY: Float): Boolean = false

    fun setPCInputProcessor(listener: PCInputProcessor?) {
        pcInputProcessor = listener
    }
}
