# Checkers AI

AI is configured in `DefaultGameConfig`:

```kotlin
val value = GameConfig(
    gameMode = GameMode.HUMAN_VS_AI,
    humanSide = PlayerColor.WHITE,
    aiDifficulty = AiDifficulty.NORMAL,
)
```

Change `aiDifficulty` there while there is no settings UI. Change `humanSide` to `BLACK` if the human should play black; the AI will then make the first white move.

## Difficulties

- `RANDOM`: chooses a random legal move. Useful for smoke tests.
- `EASY`: uses `GreedyAi`; prefers captures, longer capture chains, promotion, and avoids simple immediate exposure when possible.
- `NORMAL`: uses `MinimaxAi` with depth 3 and a 500 ms time limit.
- `HARD`: uses `MinimaxAi` with depth 5 and a 1500 ms time limit.
- `EXPERT`: uses `MinimaxAi` with depth 7 and a 3000 ms time limit. Keep this experimental on Android.

Search limits are defined in `AiConfigFactory`.

## Implementations

- `RandomAi` consumes only `CheckersRules.legalMoves(state)`.
- `GreedyAi` scores current legal moves without deep search.
- `MinimaxAi` uses depth-limited minimax with alpha-beta pruning, move ordering, and optional time limits.
- `BasicBoardEvaluator` scores material, kings, advancement, center control, and terminal wins/losses.

All AI code lives under `core/src/main/kotlin/com/infeez/simple/game/ai` and must not import libGDX or rendering classes.

## Runtime Flow

`Board` uses `GameController` as the game flow source. In `HUMAN_VS_AI`, after a legal human move, the AI move is calculated on a single-thread `ExecutorService`. The result is applied back on the libGDX render thread via `Gdx.app.postRunnable`.

Input is blocked while `TurnState.AI_THINKING` is active. Reset remains available.

## Verification

Run these after changing AI code:

```bash
./gradlew :core:test
./gradlew :desktop:compileKotlin
./gradlew :android:assembleDebug
```

Manual checks:

- Human can move only the configured human side.
- AI responds after the human move.
- Input is blocked while AI is thinking.
- AI obeys mandatory captures and capture continuations.
- Reset cancels stale AI results and starts a new game.
