package com.infeez.simple.utils

data class BoardArrayPosition(
    var indexFirst: Int = 0,
    var indexSecond: Int = 0,
) {
    fun set(boardArrayPosition: BoardArrayPosition) {
        indexFirst = boardArrayPosition.indexFirst
        indexSecond = boardArrayPosition.indexSecond
    }

    override fun toString(): String = BoardCommandUtil.checkerPositionToCommand(this)
}
