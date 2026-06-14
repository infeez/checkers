package com.infeez.simple

import com.infeez.simple.base.CheckerApplication
import com.infeez.simple.screens.GameScreen

class Main : CheckerApplication() {
    private var gameScreen: GameScreen? = null

    override fun create() {
        super.create()
        gameScreen = GameScreen(this)
        setScreen(gameScreen)
    }

    override fun dispose() {
        super.dispose()
        gameScreen?.dispose()
        ResourceSingleton.dispose()
    }
}
