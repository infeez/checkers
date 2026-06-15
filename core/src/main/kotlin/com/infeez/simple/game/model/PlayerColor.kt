package com.infeez.simple.game.model

enum class PlayerColor {
    WHITE,
    BLACK,
    ;

    fun opponent(): PlayerColor {
        return when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
    }
}
