package com.infeez.simple.game.ai

data class SearchLimits(
    val maxDepth: Int,
    val maxTimeMillis: Long? = null,
    val randomizeEqualMoves: Boolean = true,
)

object AiConfigFactory {
    fun create(difficulty: AiDifficulty): SearchLimits {
        return when (difficulty) {
            AiDifficulty.RANDOM -> SearchLimits(maxDepth = 0)
            AiDifficulty.EASY -> SearchLimits(maxDepth = 1)
            AiDifficulty.NORMAL -> SearchLimits(maxDepth = 3, maxTimeMillis = 500)
            AiDifficulty.HARD -> SearchLimits(maxDepth = 5, maxTimeMillis = 1_500)
            AiDifficulty.EXPERT -> SearchLimits(maxDepth = 7, maxTimeMillis = 3_000)
        }
    }
}
