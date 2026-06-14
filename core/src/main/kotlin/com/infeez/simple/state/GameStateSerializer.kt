package com.infeez.simple.state

import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardCommandUtil

object GameStateSerializer {
    private const val VERSION = "checkers-state-v1"
    private const val NONE = "NONE"

    fun serialize(state: GameState): String {
        return buildString {
            appendLine(VERSION)
            appendLine(state.currentTurn?.name ?: NONE)
            appendLine(state.moveNumber)

            state.board.forEach { checker ->
                    append(checker.id)
                    append(',')
                    append(checker.color.name)
                    append(',')
                    append(BoardCommandUtil.checkerPositionToCommand(checker.position))
                    append(',')
                    append(checker.isKing)
                    appendLine()
            }
        }
    }

    fun deserialize(rawState: String?): GameState? {
        if (rawState.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val lines = rawState.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList()

            require(lines.size >= 3)
            require(lines[0] == VERSION)

            val currentTurn = when (val rawTurn = lines[1]) {
                NONE -> null
                else -> CheckerColor.valueOf(rawTurn)
            }
            val moveNumber = lines[2].toInt()
            require(moveNumber >= 0)

            val checkers = lines.drop(3).map { line ->
                val parts = line.split(',')
                require(parts.size == 4)
                val id = parts[0]
                require(id.isNotBlank())
                val color = CheckerColor.valueOf(parts[1])
                val position = BoardCommandUtil.parseCommand(parts[2])
                val isKing = parts[3].toBooleanStrict()

                CheckerState(
                    id = id,
                    color = color,
                    position = BoardArrayPosition(position.indexFirst, position.indexSecond),
                    isKing = isKing,
                )
            }

            GameState(
                board = checkers,
                currentTurn = currentTurn,
                moveNumber = moveNumber,
            )
        }.getOrNull()
    }
}
