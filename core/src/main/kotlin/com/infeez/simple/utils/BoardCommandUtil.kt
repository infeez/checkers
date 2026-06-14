package com.infeez.simple.utils

object BoardCommandUtil {
    private const val FIRST_FILE = 'a'
    private const val LAST_FILE = 'h'
    private const val FIRST_RANK = '1'
    private const val LAST_RANK = '8'

    @JvmStatic
    fun parseCommand(command: String?): BoardArrayPosition {
        require(!command.isNullOrBlank()) {
            "Board command must not be null, empty, or blank."
        }
        require(command.length == 2) {
            "Board command must be exactly 2 characters."
        }

        val file = command[0]
        val rank = command[1]
        require(file in FIRST_FILE..LAST_FILE) {
            "Board command file must be in range a..h."
        }
        require(rank in FIRST_RANK..LAST_RANK) {
            "Board command rank must be in range 1..8."
        }

        return BoardArrayPosition(
            indexFirst = file - FIRST_FILE,
            indexSecond = BoardConfig.BOARD_SIZE - rank.digitToInt(),
        )
    }

    @JvmStatic
    fun checkerPositionToCommand(boardArrayPosition: BoardArrayPosition): String {
        require(boardArrayPosition.indexFirst in 0 until BoardConfig.BOARD_SIZE) {
            "Board file index must be in range 0..7."
        }
        require(boardArrayPosition.indexSecond in 0 until BoardConfig.BOARD_SIZE) {
            "Board rank index must be in range 0..7."
        }

        val file = (FIRST_FILE.code + boardArrayPosition.indexFirst).toChar()
        val rank = BoardConfig.BOARD_SIZE - boardArrayPosition.indexSecond
        return "$file$rank"
    }
}
