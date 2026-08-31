package com.infeez.simple.utils

data class BoardArrayPosition(
    val indexFirst: Int = 0,
    val indexSecond: Int = 0,
) {

    override fun toString(): String = BoardCommandUtil.checkerPositionToCommand(this)
}
