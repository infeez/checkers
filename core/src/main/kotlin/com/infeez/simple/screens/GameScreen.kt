package com.infeez.simple.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.infeez.simple.base.CheckerApplication
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.entity.Board
import com.infeez.simple.input.PCInputProcessor
import com.infeez.simple.state.GameStateStore
import com.infeez.simple.utils.BoardConfig

class GameScreen(context: CheckerApplication) : Screen {
    private val batch = GameSpriteBatch()
    private val camera = OrthographicCamera().apply {
        setToOrtho(true, BoardConfig.BOARD_PIXEL_SIZE.toFloat(), BoardConfig.BOARD_PIXEL_SIZE.toFloat())
    }
    private val viewport = FitViewport(
        BoardConfig.BOARD_PIXEL_SIZE.toFloat(),
        BoardConfig.BOARD_PIXEL_SIZE.toFloat(),
        camera,
    )
    private val board = Board(batch)
    private val stateStore = GameStateStore()
    private val inputVector = Vector3()
    private val inputProcessor = object : PCInputProcessor {
        override fun mouseDrag(x: Float, y: Float, pointer: Int): Boolean {
            val world = unproject(x, y)
            return board.mouseDrag(world.x, world.y, pointer)
        }

        override fun mouseDown(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
            val world = unproject(x, y)
            return board.mouseDown(world.x, world.y, pointer, mouseButton)
        }

        override fun mouseUp(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
            val world = unproject(x, y)
            val moved = board.mouseUp(world.x, world.y, pointer, mouseButton)
            if (moved) {
                saveGameState()
            }
            return moved
        }

        override fun touchCancelled(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
            val world = unproject(x, y)
            return board.touchCancelled(world.x, world.y, pointer, mouseButton)
        }
    }

    init {
        resize(
            Gdx.graphics?.width ?: BoardConfig.BOARD_PIXEL_SIZE,
            Gdx.graphics?.height ?: BoardConfig.BOARD_PIXEL_SIZE,
        )
        board.create()
        restoreGameStateOrStart()
        context.setPCInputProcessor(inputProcessor)
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()
        batch.projectionMatrix = camera.combined
        batch.begin()
        board.draw()
        board.update()
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        batch.projectionMatrix = camera.combined
    }

    override fun pause() {
        board.cancelActiveDrag()
        saveGameState()
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        saveGameState()
        batch.dispose()
        board.dispose()
    }

    private fun restoreGameStateOrStart() {
        val restored = stateStore.load()?.let(board::tryRestoreGameState) == true
        if (restored) {
            Gdx.app.log("CheckersState", "state restored")
        } else {
            board.startNewGame()
            Gdx.app.log("CheckersState", "new game started")
        }
    }

    private fun saveGameState() {
        stateStore.save(board.toGameState())
    }

    private fun unproject(screenX: Float, screenY: Float): Vector3 {
        inputVector.set(screenX, screenY, 0f)
        viewport.unproject(inputVector)
        return inputVector
    }
}
