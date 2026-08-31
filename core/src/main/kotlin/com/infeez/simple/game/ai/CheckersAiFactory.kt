package com.infeez.simple.game.ai

import com.infeez.simple.game.rules.CheckersRules

class CheckersAiFactory(
    private val rules: CheckersRules,
    private val evaluator: BoardEvaluator = BasicBoardEvaluator(),
    private val moveOrdering: MoveOrdering = DefaultMoveOrdering(rules),
) {
    fun create(difficulty: AiDifficulty): CheckersAi {
        return when (difficulty) {
            AiDifficulty.RANDOM -> RandomAi(rules)
            AiDifficulty.EASY -> GreedyAi(rules)
            AiDifficulty.NORMAL,
            AiDifficulty.HARD,
            AiDifficulty.EXPERT,
            -> MinimaxAi(rules, evaluator, moveOrdering)
        }
    }
}
