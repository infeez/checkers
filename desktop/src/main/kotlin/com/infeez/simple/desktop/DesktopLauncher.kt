package com.infeez.simple.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.infeez.simple.Main
import com.infeez.simple.utils.BoardConfig

fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Checkers")
        setWindowedMode(BoardConfig.BOARD_PIXEL_SIZE, BoardConfig.BOARD_PIXEL_SIZE)
        setResizable(false)
    }

    Lwjgl3Application(Main(), config)
}
